package com.sde.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sde.dto.ApiResponse;
import com.sde.dto.LoginRequest;
import com.sde.dto.PasswordReset;
import com.sde.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author Dastagiri Varada
 * @since 26/12/2020
 */

@RestController
@RequestMapping("/api/auth")
@Api(value = "Login App")
public class AuthController {

	@Autowired
	UserService userService;

	@ApiOperation(value = "This Api will allow user able to sign in", response = ApiResponse.class)
	@PostMapping(value = "/signin", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		return userService.createjwtAndSignin(loginRequest);
	}

	@ApiOperation(value = "This Api will allow user able to change the password", response = ApiResponse.class)
	@PostMapping(value = "/change")
	public ResponseEntity<ApiResponse> changePasssword(@RequestBody PasswordReset changeRequest) {
		return userService.changePassword(changeRequest);
	}

}