package com.ria.olita.tech.silingan.dto.res;

public record OtpStatusResponse(
	boolean authenticated,
	boolean otpVerified
) {
}

