package com.ria.olita.tech.silingan.exception;

public class BusinessException extends ServiceException {

	public BusinessException(String message) {
		super("BUSINESS_ERROR", message);
	}

}
