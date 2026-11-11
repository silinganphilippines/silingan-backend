package com.ria.olita.tech.silingan.exception;

import java.util.Map;

import lombok.Getter;

@Getter
public class ApiError {
	private String timestamp;
	private String code;
	private String message;
	private String path;
	private Map<String, String> errors;

	public ApiError(String code, String message, String path) {
		this.timestamp = java.time.Instant.now().toString();
		this.code = code;
		this.message = message;
		this.path = path;
	}
	public void setErrors(Map<String, String> errors) {
		this.errors = errors;
	}

}
