package com.ria.olita.tech.silingan.dto.res;

public record OtpResendResponse(
	boolean success,
	boolean challengeInitiated,
	Long cooldownSeconds
) {

	public static OtpResendResponse initiated(Long cooldownSeconds) {
		return new OtpResendResponse(true, true, cooldownSeconds);
	}
}

