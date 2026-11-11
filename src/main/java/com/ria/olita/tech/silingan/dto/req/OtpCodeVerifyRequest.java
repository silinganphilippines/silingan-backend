package com.ria.olita.tech.silingan.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record OtpCodeVerifyRequest(
	@NotBlank(message = "OTP is required")
	@Size(min = 4, max = 10, message = "OTP must be between 4 and 10 digits")
	@Pattern(regexp = "^\\d+$", message = "OTP must contain only digits")
	String otp
) {
}

