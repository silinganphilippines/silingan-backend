package com.ria.olita.tech.silingan.exception;

/**
 * Exception thrown when an OTP has expired.
 */
public class ExpiredOtpException extends ServiceException {

  private static final String CODE = "OTP_EXPIRED";

    public ExpiredOtpException() {
        super(CODE, "The OTP has expired. Please request a new one");
    }

    public ExpiredOtpException(String message) {
        super(CODE, message);
    }
}

