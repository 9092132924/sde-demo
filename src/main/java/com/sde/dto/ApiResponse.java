package com.sde.dto;

import lombok.Value;


/**
 * @author Dastagiri Varada
 * @since 26/12/2020
 */

@Value
public class ApiResponse {
	private Boolean success;
	private String message;
}