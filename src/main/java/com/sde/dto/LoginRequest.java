package com.sde.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;


/**
 * @author Dastagiri Varada
 * @since 26/12/2020
 */

@Data
public class LoginRequest {
	@NotBlank
	private String email;

	@NotBlank
	private String password;
}