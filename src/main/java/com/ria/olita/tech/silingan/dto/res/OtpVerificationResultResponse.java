package com.ria.olita.tech.silingan.dto.res;

public record OtpVerificationResultResponse(
	boolean success,
	boolean otpVerified
) {

	public static OtpVerificationResultResponse verified() {
		return new OtpVerificationResultResponse(true, true);
	}
}

