package com.ria.olita.tech.silingan.exception;


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


	@ExceptionHandler(ServiceException.class)
	public ResponseEntity<ApiError> handleServiceException(
		ServiceException ex,
		HttpServletRequest request) {

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
		HttpMessageNotReadableException ignoredEx,
		HttpServletRequest request) {

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
		Exception ignoredEx,
		HttpServletRequest request) {

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
			case "FORBIDDEN" -> HttpStatus.FORBIDDEN;
			default -> HttpStatus.INTERNAL_SERVER_ERROR;
		};
	}
}

