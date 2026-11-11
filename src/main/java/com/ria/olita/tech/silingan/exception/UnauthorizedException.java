package com.ria.olita.tech.silingan.exception;

public class UnauthorizedException extends ServiceException{

	public UnauthorizedException(String message) {
		super("UNAUTHORIZED", message);
	}

}
