package com.sde.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sde.dto.ApiResponse;
import com.sde.dto.LoginRequest;
import com.sde.dto.PasswordReset;
import com.sde.dto.SignUpRequest;
import com.sde.exception.UserAlreadyExistAuthenticationException;
import com.sde.model.User;
import com.sde.service.UserService;

/**
 * @author Dastagiri Varada
 * @since 26/12/2020
 */

@RestController
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	UserService userService;

	@PostMapping("/signup")
	public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
		try {
			userService.registerNewUser(signUpRequest);
		} catch (UserAlreadyExistAuthenticationException e) {
			return new ResponseEntity<>(new ApiResponse(false, "Email Address already in use!"),
					HttpStatus.BAD_REQUEST);
		}
		return ResponseEntity.ok().body(new ApiResponse(true,
				"verification mail has been sent to " + signUpRequest.getEmail() + ". please confirm"));
	}

	@GetMapping(value = "/confirm-account")
	public ResponseEntity<ApiResponse> confirmUserAccount(@RequestParam("confirm-token") String confirmationToken) {
		ApiResponse response = userService.validateUser(confirmationToken);
		return ResponseEntity.ok().body(response);

	}

	@PostMapping("/forgot")
	public ResponseEntity<ApiResponse> forgotPasssword(@RequestBody LoginRequest loginRequest) {
		Optional<User> userOptional = Optional.ofNullable(userService.findUserByEmail(loginRequest.getEmail()));
		if (userOptional.isPresent()) {
			userService.sendConfirmMail(loginRequest);
			return ResponseEntity.ok().body(new ApiResponse(true,
					"password reset mail has been sent to " + loginRequest.getEmail() + ". please confirm"));
		} else
			return ResponseEntity.ok().body(new ApiResponse(false, "User is not exists with us. Please check"));
	}

	@PostMapping("/reset")
	public ResponseEntity<ApiResponse> resetPassword(@RequestBody PasswordReset resetRequest,
			@RequestParam("confirm-token") String token) {
		return userService.resetPassword(token, resetRequest.getPassword());
	}

	@GetMapping("/all")
	public ResponseEntity<Object> getContent() {
		return ResponseEntity.ok("All the User content will go here."
				+ "currently showing Sde Details");
	}

}