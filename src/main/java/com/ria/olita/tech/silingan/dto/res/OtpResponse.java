package com.ria.olita.tech.silingan.dto.res;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response DTO for OTP operations.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OtpResponse {

    /**
     * Response message.
     */
    private String message;

    /**
     * Time in seconds until next OTP request is allowed.
     * Only present when relevant.
     */
    private Long cooldownSeconds;

    /**
     * Factory method for successful OTP send.
     */
    public static OtpResponse otpSent() {
        return OtpResponse.builder()
                .message("OTP sent successfully")
                .build();
    }

    /**
     * Factory method for successful OTP send with cooldown info.
     */
    public static OtpResponse otpSentWithCooldown(long cooldownSeconds) {
        return OtpResponse.builder()
                .message("OTP sent successfully")
                .cooldownSeconds(cooldownSeconds)
                .build();
    }

    /**
     * Factory method for successful OTP verification.
     */
    public static OtpResponse verified() {
        return OtpResponse.builder()
                .message("OTP verified successfully")
                .build();
    }
}

