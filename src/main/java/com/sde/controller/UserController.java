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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author Dastagiri Varada
 * @since 26/12/2020
 */

@RestController
@RequestMapping("/api/user")
@Api(value = "Login App")
public class UserController {

	@Autowired
	UserService userService;

	@ApiOperation(value = "This Api will allow user to register with requred information", response = ApiResponse.class)
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

	@ApiOperation(value = "This Api will allow user to confirm account using mail received", response = ApiResponse.class)
	@GetMapping(value = "/confirm-account")
	public ResponseEntity<ApiResponse> confirmUserAccount(@RequestParam("confirm-token") String confirmationToken) {
		ApiResponse response = userService.validateUser(confirmationToken);
		return ResponseEntity.ok().body(response);

	}

	@ApiOperation(value = "This Api will allow user to send forgot password mail to Users", response = ApiResponse.class)
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

	@ApiOperation(value = "This Api will allow user to reset the password as per user request", response = ApiResponse.class)
	@PostMapping("/reset")
	public ResponseEntity<ApiResponse> resetPassword(@RequestBody PasswordReset resetRequest,
			@RequestParam("confirm-token") String token) {
		return userService.resetPassword(token, resetRequest.getPassword());
	}

	@ApiOperation(value = "This Api will allow user to see the public content", response = ApiResponse.class)
	@GetMapping("/all")
	public ResponseEntity<Object> getContent() {
		return ResponseEntity.ok("All the User content will go here." + "currently showing Sde Details");
	}

}