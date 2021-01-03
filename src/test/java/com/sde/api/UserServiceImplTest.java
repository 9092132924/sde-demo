package com.sde.api;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sde.config.AppProperties;
import com.sde.dto.ApiResponse;
import com.sde.dto.LoginRequest;
import com.sde.dto.PasswordReset;
import com.sde.dto.SignUpRequest;
import com.sde.exception.UserAlreadyExistAuthenticationException;
import com.sde.mail.EmailSenderService;
import com.sde.model.ConfirmationToken;
import com.sde.model.User;
import com.sde.repo.ConfirmationTokenRepository;
import com.sde.repo.ProfileRepository;
import com.sde.repo.UserRepository;
import com.sde.security.jwt.TokenProvider;
import com.sde.service.UserService;
import com.sde.service.UserServiceImpl;

@SpringBootTest
class UserServiceImplTest {

	@Mock
	UserRepository userRepository;

	@Mock
	ProfileRepository profileRepository;

	@Mock
	ConfirmationTokenRepository confirmationTokenRepository;
	@Mock
	EmailSenderService javaMailSender;
	@Mock
	private AppProperties appProperties = new AppProperties();

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	AuthenticationManager authenticationManager;

	@Mock
	TokenProvider tokenProvider;

	@InjectMocks
	private UserService userService = new UserServiceImpl();

	@BeforeEach
	void setUp() throws Exception {
		appProperties.setForwardMailExpiryTime(30l);
		when(profileRepository.save(any())).thenReturn(null);
		when(confirmationTokenRepository.save(any())).thenReturn(null);
		when(userRepository.existsByEmail(any())).thenReturn(false);
		when(userRepository.existsById(any())).thenReturn(false);
		when(passwordEncoder.encode(any())).thenReturn("abc");
		when(profileRepository.save(any())).thenReturn(null);
		when(userRepository.save(any())).thenReturn(new User());
		when(confirmationTokenRepository.save(any())).thenReturn(null);
		when(appProperties.getTokenSecret()).thenReturn("secret");
		when(appProperties.getConfirmAccount()).thenReturn("abc");

	}
	
	@Test
	void testSuccessfullresetPwd() {
		User user = new User();
		user.setEmail("abc@gmail.com");
		user.setPassword("123456");
		user.setForgotPwdCreatedDate(LocalDateTime.now());
		user.setAccountVerified(true);
		when(userRepository.findByForgotPwdToken(any())).thenReturn(user);
		when(passwordEncoder.encode(any())).thenReturn("abc");
		PasswordReset passwordReset = new PasswordReset();
		passwordReset.setEmail("abc@gmail.com");
		passwordReset.setPassword("abc@gmail.com");
		ResponseEntity<ApiResponse> res = userService.resetPassword("token", "1234567");
		when(appProperties.getForwardMailExpiryTime()).thenReturn(30L);
		assertEquals("password Link got expired!", res.getBody().getMessage());
	}

	@Test
	void testErrorResetPwd() {

		when(userRepository.findByForgotPwdToken(any())).thenReturn(null);
		ResponseEntity<ApiResponse> res = userService.resetPassword("token", "1234567");

		assertEquals("invalid reset password url", res.getBody().getMessage());
	}

	@Test
	void testSuccessFullRegistration() {
		Mockito.lenient().doNothing().when(javaMailSender).sendEmail(any());
		SignUpRequest signup = new SignUpRequest("1", "test", "abc@gmail.com", "123456");
		signup.setMatchingPassword("123456");
		User user = userService.registerNewUser(signup);
		assertNotNull(user);
	}

	@Test
	void testFailedRegistration() {
		when(userRepository.existsByEmail(any())).thenReturn(true);
		SignUpRequest signup = new SignUpRequest("1", "test", "abc@gmail.com", "123456");
		signup.setMatchingPassword("123456");
		Assertions.assertThrows(UserAlreadyExistAuthenticationException.class, () -> {
			userService.registerNewUser(signup);
		});
	}

	@Test
	void testFailedRegistrationUserExists() {
		when(userRepository.existsById(any())).thenReturn(true);
		SignUpRequest signup = new SignUpRequest("1", "test", "abc@gmail.com", "123456");
		signup.setUserID(1l);
		signup.setMatchingPassword("123456");
		Assertions.assertThrows(UserAlreadyExistAuthenticationException.class, () -> {
			userService.registerNewUser(signup);
		});
	}

	@Test
	void testErrorVerifyAccount() {
		User user = new User();
		user.setEmail("abc@gmail.com");
		user.setPassword("123456");
		LoginRequest login = new LoginRequest();
		login.setEmail("abc@gmail.com");
		login.setPassword("123456");
		when(userRepository.findByEmail(any())).thenReturn(user);
		Mockito.when(authenticationManager.authenticate(any())).thenReturn(null);
		Object response = userService.createjwtAndSignin(login);
		assertNotNull(response);
	}

	@Test
	void testErrorNoUser() {
		LoginRequest login = new LoginRequest();
		login.setEmail("abc@gmail.com");
		login.setPassword("123456");
		when(userRepository.findByEmail(any())).thenReturn(null);
		Mockito.when(authenticationManager.authenticate(any())).thenReturn(null);
		Object response = userService.createjwtAndSignin(login);
		assertNotNull(response);
	}

	@Test
	void testSuccessfullLogin() {
		User user = new User();
		user.setEmail("abc@gmail.com");
		user.setPassword("123456");
		user.setAccountVerified(true);
		LoginRequest login = new LoginRequest();
		login.setEmail("abc@gmail.com");
		login.setPassword("123456");
		when(userRepository.findByEmail(any())).thenReturn(user);
		Mockito.when(authenticationManager.authenticate(any())).thenReturn(null);
		Object response = userService.createjwtAndSignin(login);
		assertNotNull(response);
	}

	@Test
	void testErorLoginDisabledException() {
		User user = new User();
		user.setAccountVerified(true);
		LoginRequest login = new LoginRequest();
		login.setEmail("abc@gmail.com");
		login.setPassword("123456");
		when(userRepository.findByEmail(any())).thenReturn(user);
		Mockito.when(authenticationManager.authenticate(any())).thenThrow(DisabledException.class);

		Object response = userService.createjwtAndSignin(login);
		assertNotNull(response);
	}

	@Test
	void testErorLoginBadCredentialsException() {
		User user = new User();
		user.setAccountVerified(true);
		LoginRequest login = new LoginRequest();
		login.setEmail("abc@gmail.com");
		login.setPassword("123456");
		when(userRepository.findByEmail(any())).thenReturn(user);
		Mockito.when(authenticationManager.authenticate(any())).thenThrow(BadCredentialsException.class);
		Object response = userService.createjwtAndSignin(login);
		assertNotNull(response);
	}

	@SuppressWarnings("deprecation")
	@Test
	void testSuccessfullValidateUser() {
		User user = new User();
		user.setEmail("abc@gmail.com");
		user.setPassword("123456");
		user.setAccountVerified(true);
		ConfirmationToken token = new ConfirmationToken();
		token.setUser(user);
		token.setExpiryDate(new Date(2030, 1, 2));
		when(confirmationTokenRepository.findByConfirmToken(any())).thenReturn(token);
		when(userRepository.findByEmail(any())).thenReturn(user);
		Object response = userService.validateUser("token");
		assertNotNull(response);
	}

	@Test
	void testErrorValidateUser() {
		User user = new User();
		user.setEmail("abc@gmail.com");
		user.setPassword("123456");
		user.setAccountVerified(true);
		ConfirmationToken token = new ConfirmationToken();
		token.setUser(user);
		token.setExpiryDate(new Date(0000001));
		when(confirmationTokenRepository.findByConfirmToken(any())).thenReturn(token);
		Mockito.lenient().doNothing().when(confirmationTokenRepository).delete(any());
		Mockito.lenient().doNothing().when(userRepository).delete(any());
		when(userRepository.findByEmail(any())).thenReturn(user);
		Object response = userService.validateUser("token");
		assertNotNull(response);
	}

	@Test
	void testSuccessfullChangePwd() {
		User user = new User();
		user.setEmail("abc@gmail.com");
		user.setPassword("123456");
		user.setAccountVerified(true);
		when(userRepository.findByEmail(any())).thenReturn(user);
		when(passwordEncoder.encode(any())).thenReturn("abc");
		PasswordReset passwordReset = new PasswordReset();
		passwordReset.setEmail("abc@gmail.com");
		passwordReset.setPassword("abc@gmail.com");
		Object response = userService.changePassword(passwordReset);
		assertNotNull(response);
	}



	@Test
	void testSuccessfullSentConfirmMail() {
		User user = new User();
		user.setEmail("abc@gmail.com");
		user.setPassword("123456");
		user.setForgotPwdCreatedDate(LocalDateTime.now());
		user.setAccountVerified(true);
		when(userRepository.findByEmail(any())).thenReturn(user);
		when(passwordEncoder.encode(any())).thenReturn("abc");
		LoginRequest login = new LoginRequest();
		login.setEmail("abc@gmail.com");
		login.setPassword("123456");
		when(appProperties.getForgotaccount()).thenReturn("");
		userService.sendConfirmMail(login);

	}

}
