package com.ria.olita.tech.silingan.exception;

public class NotFoundException extends ServiceException {

	public NotFoundException(String message) {
		super("NOT_FOUND", message);
	}
}
