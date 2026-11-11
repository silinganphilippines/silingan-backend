package com.ria.olita.tech.silingan.exception;

/**
 * Exception thrown when an invalid phone number format is provided.
 */
public class InvalidPhoneNumberException extends ServiceException {

    private static final String CODE = "INVALID_PHONE_NUMBER";

    public InvalidPhoneNumberException() {
        super(CODE, "Invalid Philippine mobile number format. Expected format: +639XXXXXXXXX");
    }

    public InvalidPhoneNumberException(String message) {
        super(CODE, message);
    }
}

