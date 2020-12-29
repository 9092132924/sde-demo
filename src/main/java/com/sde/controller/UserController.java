package com.sde.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.sde.model.User;
import com.sde.service.UserService;


/**
 * @author Dastagiri Varada
 * @since 26/12/2020
 */

@RestController
@RequestMapping("/api")
public class UserController {

	@Autowired
	UserService userService;

	@GetMapping("/all")
	public ResponseEntity<Object> getContent() {
		return ResponseEntity.ok("hello home page details");
	}

	@PostMapping("/forgot")
	public ResponseEntity<Object> forgotPasssword(@RequestBody LoginRequest loginRequest) {
		Optional<User> userOptional = Optional.ofNullable(userService.findUserByEmail(loginRequest.getEmail()));
		if (userOptional.isPresent()) {
			userService.sendConfirmMail(loginRequest);
			return ResponseEntity.ok().body(new ApiResponse(true,
					"password reset mail has been sent to " + loginRequest.getEmail() + ". please confirm"));
		} else
			return ResponseEntity.ok().body(new ApiResponse(false, "Email Address not exists!"));
	}

	@PostMapping("/reset")
	public ResponseEntity<ApiResponse> resetPasssword(@RequestBody PasswordReset resetRequest,
			@RequestParam("confirm-token") String token) {
		return userService.resetPassword(token, resetRequest.getPassword());
	}

	@PostMapping("/change")
	public ResponseEntity<ApiResponse> changePasssword(@RequestBody PasswordReset changeRequest) {
		return userService.changePassword(changeRequest);
	}

}