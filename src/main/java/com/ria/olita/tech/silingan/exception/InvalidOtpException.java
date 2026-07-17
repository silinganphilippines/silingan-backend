package com.ria.olita.tech.silingan.exception;

/**
 * Exception thrown when an invalid OTP is provided during verification.
 */
public class InvalidOtpException extends ServiceException {

    private static final String CODE = "INVALID_OTP";

    public InvalidOtpException() {
        super(CODE, "The OTP provided is invalid");
    }

    public InvalidOtpException(String message) {
        super(CODE, message);
    }

    public InvalidOtpException(int remainingAttempts) {
        super(CODE, String.format("Invalid OTP. %d attempt(s) remaining", remainingAttempts));
    }
}

