package com.sde.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sde.config.AppProperties;
import com.sde.dto.ApiResponse;
import com.sde.dto.JwtAuthenticationResponse;
import com.sde.dto.LoginRequest;
import com.sde.dto.PasswordReset;
import com.sde.dto.SignUpRequest;
import com.sde.exception.UserAlreadyExistAuthenticationException;
import com.sde.mail.EmailSenderService;
import com.sde.model.ConfirmationToken;
import com.sde.model.Profile;
import com.sde.model.Role;
import com.sde.model.User;
import com.sde.repo.ConfirmationTokenRepository;
import com.sde.repo.ProfileRepository;
import com.sde.repo.UserRepository;
import com.sde.security.jwt.TokenProvider;
/**
 * @author Dastagiri Varada
 * @since 26/12/2020
 */

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
	private AppProperties appProperties;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	AuthenticationManager authenticationManager;
	@Autowired
	private ConfirmationTokenRepository confirmationTokenRepository;

	@Autowired
	private EmailSenderService emailSenderService;
	@Autowired
	TokenProvider tokenProvider;

	public UserServiceImpl(AppProperties appProperties) {
		this.appProperties = appProperties;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
				new ArrayList<>());
	}

	@Override
	@Transactional
	public User registerNewUser(final SignUpRequest signUpRequest) throws UserAlreadyExistAuthenticationException {
		if (signUpRequest.getUserID() != null && userRepository.existsById(signUpRequest.getUserID())) {
			throw new UserAlreadyExistAuthenticationException(
					"User with User id " + signUpRequest.getUserID() + " already exist");
		} else if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			throw new UserAlreadyExistAuthenticationException(
					"User with email id " + signUpRequest.getEmail() + " already exist");
		}
		User user = buildUser(signUpRequest);
		Date now = Calendar.getInstance().getTime();
		user.setCreatedDate(now);
		profileRepository.save(user.getProfile());
		profileRepository.flush();
		user = userRepository.save(user);
		userRepository.flush();
		sendconfirmationMail(user);
		return user;
	}

	private void sendconfirmationMail(User user) {
		ConfirmationToken confirmationToken = new ConfirmationToken(user);
		confirmationTokenRepository.save(confirmationToken);
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(user.getEmail());
		mailMessage.setSubject("Complete Registration!");
		mailMessage.setFrom("ramu6214@gmail.com");
		mailMessage.setText("To confirm your account, please click here : "
				+ appProperties.getAuth().getConfirmaccount() + confirmationToken.getConfirmToken());
		emailSenderService.sendEmail(mailMessage);

	}

	private User buildUser(final SignUpRequest formDTO) {
		User user = new User();
		user.setName(formDTO.getName());
		user.setEmail(formDTO.getEmail());
		Profile profile=new Profile();
		profile.setEmail(user.getEmail());
		profile.setName(formDTO.getName());
		Date now = Calendar.getInstance().getTime();
		profile.setCreatedDate(now);
		user.setProfile(profile);
		user.setPassword(passwordEncoder.encode(formDTO.getPassword()));
		final HashSet<Role> roles = new HashSet<>();
		user.setRoles(roles);
		return user;
	}

	@Override
	public User findUserByEmail(final String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public Optional<User> findUserById(Long id) {
		return userRepository.findById(id);
	}

	@Transactional(value = "transactionManager")
	@Override
	public ApiResponse validateUser(String confirmationToken) {
		ConfirmationToken token = confirmationTokenRepository.findByConfirmToken(confirmationToken);
		User user = userRepository.findByEmail(token.getUser().getEmail());
		if (token.getExpiryDate().getTime() - Calendar.getInstance().getTime().getTime() > 0) {
			user.setAccountVerified(true);
			userRepository.save(user);
			return new ApiResponse(true, "Congratulations! Your account has been activated and email is verified!");
		} else {
			confirmationTokenRepository.delete(token);
			userRepository.delete(user);
			return new ApiResponse(false, "The ativation link expired .Please reregister");
		}

	}

	@Transactional(value = "transactionManager")
	@Override
	public void sendConfirmMail(LoginRequest loginRequest) {
		User user = userRepository.findByEmail(loginRequest.getEmail());
		user.setForgotPwdToken(generateToken());
		user.setForgotPwdCreatedDate(LocalDateTime.now());
		user = userRepository.save(user);
		sendResetMail(user.getForgotPwdToken(), loginRequest.getEmail());
	}

	private void sendResetMail(String token, String email) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(email);
		mailMessage.setSubject("Forgot Password!");
		mailMessage.setFrom("ramu6214@gmail.com");
		mailMessage.setText(
				"To confirm your account, please click here : " + appProperties.getAuth().getForgotaccount() + token);
		emailSenderService.sendEmail(mailMessage);

	}

	private String generateToken() {
		StringBuilder token = new StringBuilder();
		return token.append(UUID.randomUUID().toString()).append(UUID.randomUUID().toString()).toString();
	}

	/**
	 * Check whether the created token expired or not.
	 * 
	 * @param tokenCreationDate
	 * @return true or false
	 */
	private boolean isTokenExpired(final LocalDateTime tokenCreationDate) {

		LocalDateTime now = LocalDateTime.now();
		Duration diff = Duration.between(tokenCreationDate, now);
		return diff.toMinutes() >= appProperties.getAuth().getForwardMailExpiryTime();
	}

	@Override
	public ResponseEntity<ApiResponse> resetPassword(String token, String password) {
		Optional<User> userOptional = Optional.ofNullable(userRepository.findByForgotPwdToken(token));
		if (!userOptional.isPresent()) {
			return ResponseEntity.ok().body(new ApiResponse(false, "invalid reset password url"));
		}
		LocalDateTime tokenCreationDate = userOptional.get().getForgotPwdCreatedDate();
		if (isTokenExpired(tokenCreationDate)) {
			return ResponseEntity.ok().body(new ApiResponse(false, "password Link got expired!"));
		}
		User user = userOptional.get();
		user.setPassword(passwordEncoder.encode(password));
		user.setForgotPwdToken(null);
		user.setForgotPwdCreatedDate(null);
		userRepository.save(user);
		return ResponseEntity.ok().body(new ApiResponse(true, "Your password successfully updated"));
	}

	@Override
	public ResponseEntity<Object> createjwtAndSignin(@Valid LoginRequest loginRequest) {
		if (null != findUserByEmail(loginRequest.getEmail())) {
			if (findUserByEmail(loginRequest.getEmail()).isAccountVerified()) {
				try {
					authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
							loginRequest.getPassword()));
				} catch (DisabledException e) {
					return new ResponseEntity<>(new ApiResponse(false, "User is disabled!"), HttpStatus.BAD_REQUEST);
				} catch (BadCredentialsException e) {
					return new ResponseEntity<>(new ApiResponse(false, "invalid user name and password!"),
							HttpStatus.BAD_REQUEST);
				}
				final UserDetails userDetails = loadUserByUsername(loginRequest.getEmail());
				String jwt = tokenProvider.createToken(userDetails);
				return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, findUserByEmail(loginRequest.getEmail())));
			} else {
				return ResponseEntity.ok().body("user yet to verify email .Plese confirm before login");
			}
		} else {
			return ResponseEntity.ok().body("No account exists with us.Please signup");
		}
	}



	@Override
	public ResponseEntity<ApiResponse> changePassword(PasswordReset passwordreset) {
		User user = userRepository.findByEmail(passwordreset.getEmail());
		user.setPassword(passwordEncoder.encode(passwordreset.getPassword()));
		userRepository.save(user);
		return ResponseEntity.ok().body(new ApiResponse(true, "Your password successfully updated"));
		
	}
}
