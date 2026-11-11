package com.ria.olita.tech.silingan.service.sms;

/**
 * Interface for SMS provider implementations.
 * Allows for easy swapping of SMS providers (Semaphore, Twilio, etc.)
 */
public interface SmsProvider {

    /**
     * Sends an SMS message to the specified phone number.
     *
     * @param phoneNumber the recipient's phone number in international format
     * @param message     the SMS message content
     * @return true if the message was sent successfully, false otherwise
     */
    boolean sendSms(String phoneNumber, String message);

    /**
     * Sends an OTP SMS message to the specified phone number.
     *
     * @param phoneNumber the recipient's phone number in international format
     * @param otp         the OTP code to send
     * @return true if the message was sent successfully, false otherwise
     */
    default boolean sendOtp(String phoneNumber, String otp) {
        String message = String.format("Welcome to Silingan! Your verification code is %s. It expires in 5 minutes. If you didn’t request this, you can safely ignore this message.",otp);
        return sendSms(phoneNumber, message);
    }

    /**
     * Gets the provider name.
     *
     * @return the name of this SMS provider
     */
    String getProviderName();

    /**
     * Checks if the provider is configured and ready to use.
     *
     * @return true if the provider is properly configured
     */
    boolean isConfigured();
}

