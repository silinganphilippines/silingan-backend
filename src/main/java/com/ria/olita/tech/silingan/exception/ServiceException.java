package com.ria.olita.tech.silingan.exception;

public abstract class ServiceException extends RuntimeException {

	private final String code;

	protected ServiceException(String code, String message) {
		super(message);
		this.code = code;
	}

	public String getCode() {
		return code;
	}

}
