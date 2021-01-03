package com.sde.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.sde.controller.UserController;
import com.sde.dto.ApiResponse;
import com.sde.dto.LoginRequest;
import com.sde.dto.PasswordReset;
import com.sde.dto.SignUpRequest;
import com.sde.exception.UserAlreadyExistAuthenticationException;
import com.sde.model.User;
import com.sde.service.UserServiceImpl;

@SpringBootTest
class UserControllerTest {

	@InjectMocks
	private UserController userController;

	@Mock
	private UserServiceImpl userService;

	@BeforeEach
	void setUp() throws Exception {

	}

	@Test
	void testSuccessCaseRegisterUser() {
		SignUpRequest req = new SignUpRequest(null, "abc@gmail.com", null, null);
		when(userService.registerNewUser(any())).thenReturn(null);
		ResponseEntity<ApiResponse> res = userController.registerUser(req);
		assertEquals("verification mail has been sent to " + req.getEmail() + ". please confirm",
				res.getBody().getMessage());
		assertEquals("OK", res.getStatusCode().getReasonPhrase());

	}

	@Test
	void testErrorCaseRegisterUser() {
		SignUpRequest req = new SignUpRequest(null, "abc@gmail.com", null, null);
		when(userService.registerNewUser(any())).thenThrow(UserAlreadyExistAuthenticationException.class);
		ResponseEntity<ApiResponse> res = userController.registerUser(req);
		assertEquals("Email Address already in use!", res.getBody().getMessage());
		assertEquals("Bad Request", res.getStatusCode().getReasonPhrase());
	}

	@Test
	void testSuccessCaseConfirmUserAccount() {
		ApiResponse req = new ApiResponse(true, "confirmed");
		when(userService.validateUser(any())).thenReturn(req);
		ResponseEntity<ApiResponse> res = userController.confirmUserAccount("abc");
		assertEquals("confirmed", res.getBody().getMessage());
		assertEquals(true, res.getBody().getSuccess());

	}

	@Test
	void testSuccessCaseForgotPasssword() {
		LoginRequest req = new LoginRequest();
		req.setEmail("abc@gmail.com");
		when(userService.findUserByEmail(any())).thenReturn(new User());
		ResponseEntity<ApiResponse> res = userController.forgotPasssword(req);
		assertEquals("password reset mail has been sent to " + req.getEmail() + ". please confirm",
				res.getBody().getMessage());
		assertEquals(true, res.getBody().getSuccess());

	}

	@Test
	void testErrorCaseForgotPasssword() {
		LoginRequest req = new LoginRequest();
		req.setEmail("abc@gmail.com");
		when(userService.findUserByEmail(any())).thenReturn(null);
		ResponseEntity<ApiResponse> res = userController.forgotPasssword(req);
		assertEquals("User is not exists with us. Please check", res.getBody().getMessage());
		assertEquals(false, res.getBody().getSuccess());

	}

	@Test
	void testSuccessCaseresetPasssword() {
		ApiResponse respons = new ApiResponse(true,"Your password successfully updated");
		ResponseEntity<ApiResponse> response=new ResponseEntity<ApiResponse>(respons, HttpStatus.OK);
		PasswordReset pwdReq=new PasswordReset();
		pwdReq.setPassword("abc");
		when(userService.resetPassword(any(),any())).thenReturn(response);
		ResponseEntity<ApiResponse> res = userController.resetPassword(pwdReq, "abbc");
		assertEquals("Your password successfully updated", res.getBody().getMessage());
		assertEquals(true, res.getBody().getSuccess());

	}

	@Test
	void testSuccessCasegetContent() {

		ResponseEntity<Object> res = userController.getContent();
		assertEquals("All the User content will go here.currently showing Sde Details", res.getBody());
	}

}
