package com.ria.olita.tech.silingan.exception;

public class ConflictException extends ServiceException {

	public ConflictException(String message) {
		super("CONFLICT", message);
	}
}


