package com.ria.olita.tech.silingan.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ServiceException.class)
	public ResponseEntity<ApiError> handleServiceException(
		ServiceException ex,
		HttpServletRequest request) {

		log.warn("ServiceException at {}: {} - {}", request.getRequestURI(), ex.getCode(), ex.getMessage());

		ApiError error = new ApiError(
			ex.getCode(),
			ex.getMessage(),
			request.getRequestURI()
		);

		return ResponseEntity
			.status(mapStatus(ex.getCode()))
			.body(error);
	}


	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidationErrors(
		MethodArgumentNotValidException ex,
		HttpServletRequest request) {

		log.warn("Validation error at {}: {}", request.getRequestURI(), ex.getMessage());

		Map<String, String> errors = new HashMap<>();

		ex.getBindingResult()
			.getFieldErrors()
			.forEach(err ->
				errors.put(err.getField(), err.getDefaultMessage())
			);

		ApiError apiError = new ApiError(
			"VALIDATION_ERROR",
			"Validation failed",
			request.getRequestURI()
		);

		apiError.setErrors(errors);

		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(apiError);
	}


	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiError> handleInvalidJson(
		HttpMessageNotReadableException ex,
		HttpServletRequest request) {

		log.warn("Invalid JSON at {}: {}", request.getRequestURI(), ex.getMessage());

		ApiError error = new ApiError(
			"INVALID_REQUEST",
			"Malformed JSON request",
			request.getRequestURI()
		);

		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(error);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleGeneral(
		Exception ex,
		HttpServletRequest request) {

		log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage());
		log.error("Stack trace: ", ex);

		ApiError error = new ApiError(
			"INTERNAL_ERROR",
			"Something went wrong",
			request.getRequestURI()
		);

		return ResponseEntity
			.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(error);
	}


	private HttpStatus mapStatus(String code) {
		return switch (code) {
			case "NOT_FOUND" -> HttpStatus.NOT_FOUND;
			case "CONFLICT" -> HttpStatus.CONFLICT;
			case "VALIDATION_ERROR" -> HttpStatus.BAD_REQUEST;
			case "UNAUTHORIZED" -> HttpStatus.UNAUTHORIZED;
			case "FORBIDDEN", "OTP_REQUIRED" -> HttpStatus.FORBIDDEN;
			case "INVALID_OTP", "INVALID_PHONE_NUMBER" -> HttpStatus.BAD_REQUEST;
			case "OTP_EXPIRED", "EXPIRED_OTP" -> HttpStatus.GONE;
			case "OTP_ATTEMPTS_EXCEEDED", "TOO_MANY_ATTEMPTS", "OTP_COOLDOWN" -> HttpStatus.TOO_MANY_REQUESTS;
			default -> HttpStatus.INTERNAL_SERVER_ERROR;
		};
	}
}

