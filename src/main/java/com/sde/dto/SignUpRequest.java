package com.sde.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.sde.validator.PasswordMatches;

import lombok.Data;

/**
 * @author Dastagiri Varada
 * @since 26/12/2020
 */

@Data
@PasswordMatches
public class SignUpRequest {

	private Long userID;

	private String providerUserId;

	@NotEmpty
	private String name;

	@NotEmpty
	private String email;

	@Size(min = 6, message = "{Size.userDto.password}")
	private String password;

	@NotEmpty
	private String matchingPassword;

	public SignUpRequest(String providerUserId, String name, String email, String password) {
		this.providerUserId = providerUserId;
		this.name = name;
		this.email = email;
		this.password = password;
	}

	public static Builder getBuilder() {
		return new Builder();
	}

	public static class Builder {
		private String providerUserID;
		private String name;
		private String email;
		private String password;

		public Builder addProviderUserID(final String userID) {
			this.providerUserID = userID;
			return this;
		}

		public Builder addName(final String name) {
			this.name = name;
			return this;
		}

		public Builder addEmail(final String email) {
			this.email = email;
			return this;
		}

		public Builder addPassword(final String password) {
			this.password = password;
			return this;
		}

		public SignUpRequest build() {
			return new SignUpRequest(providerUserID, name, email, password);
		}
	}
}
