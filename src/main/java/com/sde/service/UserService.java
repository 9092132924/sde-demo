package com.sde.service;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.sde.dto.ApiResponse;
import com.sde.dto.LoginRequest;
import com.sde.dto.PasswordReset;
import com.sde.dto.SignUpRequest;
import com.sde.exception.UserAlreadyExistAuthenticationException;
import com.sde.model.User;

/**
 * @author Dastagiri Varada
 * @since 26/12/2020
 */
public interface UserService {

	public User registerNewUser(SignUpRequest signUpRequest) throws UserAlreadyExistAuthenticationException;

	User findUserByEmail(String email);

	Optional<User> findUserById(Long id);

	UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

	public ApiResponse validateUser(String confirmationToken);

	public void sendConfirmMail(LoginRequest loginRequest);

	public ResponseEntity<ApiResponse> resetPassword(String token, String password);

	public ResponseEntity<Object> createjwtAndSignin(@Valid LoginRequest loginRequest);

	public ResponseEntity<ApiResponse> changePassword(PasswordReset changeRequest);

}
