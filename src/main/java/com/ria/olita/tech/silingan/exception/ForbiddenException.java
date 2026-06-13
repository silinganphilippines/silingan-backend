package com.ria.olita.tech.silingan.exception;

public class ForbiddenException extends ServiceException {

	public ForbiddenException(String message) {
		super("FORBIDDEN", message);
	}

}
