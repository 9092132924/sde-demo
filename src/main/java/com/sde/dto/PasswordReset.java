package com.sde.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * @author Dastagiri Varada
 * @since 26/12/2020
 */

@Data
public class PasswordReset {
	@NotBlank
	private String matchingPassword;

	@Size(min = 6, message = "{Size.userDto.password}")
	@NotBlank
	private String password;

	private String email;
}