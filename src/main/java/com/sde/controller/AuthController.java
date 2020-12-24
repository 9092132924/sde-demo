package com.sde.controller;

import javax.servlet.http.HttpServletResponse;
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
import com.sde.dto.SignUpRequest;
import com.sde.exception.UserAlreadyExistAuthenticationException;
import com.sde.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {


	@Autowired
	UserService userService;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) throws Exception {

		return userService.createjwtAndSignin(loginRequest);
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
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
	public ResponseEntity<?> confirmUserAccount(HttpServletResponse httpServletRespons,
			@RequestParam("confirm-token") String confirmationToken) {
		ApiResponse response = userService.validateUser(confirmationToken);
		return ResponseEntity.ok().body(response);

	}

}