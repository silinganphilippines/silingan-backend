package com.ria.olita.tech.silingan.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Configuration properties for SMS providers.
 */
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "sms")
public class SmsProperties {

    /**
     * Active SMS provider (e.g., "semaphore", "twilio").
     */
    @NotBlank
    private String provider = "semaphore";

    /**
     * When enabled, outbound SMS sending is bypassed and treated as successful.
     * Useful for development/staging when provider setup is incomplete.
     */
    private boolean bypassSending = false;

    /**
     * Semaphore SMS provider configuration.
     */
    private Semaphore semaphore = new Semaphore();

    @Getter
    @Setter
    public static class Semaphore {
        /**
         * Semaphore API key.
         */
        private String apiKey;

        /**
         * Sender name for SMS messages.
         */
        private String senderName = "SILINGAN";
    }
}

