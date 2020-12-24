package com.sde.dto;

import java.io.Serializable;

import com.sde.model.User;

import lombok.Value;

@Value
public class JwtAuthenticationResponse implements Serializable  {

	private static final long serialVersionUID = -8091879091924046844L;
	private final String jwttoken;
	private final User user;

	public JwtAuthenticationResponse(String jwttoken,User user) {
		this.jwttoken = jwttoken;
		this.user=user;
	}

	public String getToken() {
		return this.jwttoken;
	}
}