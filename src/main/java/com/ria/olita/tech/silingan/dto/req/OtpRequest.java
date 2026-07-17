package com.ria.olita.tech.silingan.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO for OTP request operation.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpRequest {

    /**
     * Philippine mobile number in international format.
     * Valid formats: +639XXXXXXXXX or 09XXXXXXXXX
     */
    @NotBlank(message = "Mobile number is required")
    @Pattern(
        regexp = "^(\\+63|0)9\\d{9}$",
        message = "Invalid Philippine mobile number format. Expected: +639XXXXXXXXX or 09XXXXXXXXX"
    )
    private String mobileNumber;
}

