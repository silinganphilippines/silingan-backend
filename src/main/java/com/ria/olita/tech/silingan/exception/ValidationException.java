package com.ria.olita.tech.silingan.exception;

public class ValidationException extends ServiceException {
	public ValidationException(String message) {
		super("VALIDATION_ERROR", message);
	}

}
