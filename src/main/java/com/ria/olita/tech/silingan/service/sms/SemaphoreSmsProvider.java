package com.ria.olita.tech.silingan.service.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.ria.olita.tech.silingan.config.SmsProperties;

import lombok.RequiredArgsConstructor;

/**
 * Semaphore SMS provider implementation.
 * Integrates with Semaphore PH SMS gateway for Philippine mobile numbers.
 *
 * @see <a href="https://semaphore.co/docs">Semaphore API Documentation</a>
 */
@Service
@RequiredArgsConstructor
public class SemaphoreSmsProvider implements SmsProvider {

	private static final Logger log = LoggerFactory.getLogger(SemaphoreSmsProvider.class);
	private static final String PROVIDER_NAME = "Semaphore";

	private final SmsProperties smsProperties;

	@Override
	public boolean sendSms(String phoneNumber, String message) {
		if (!isConfigured()) {
			log.warn("Semaphore SMS provider is not properly configured. Skipping SMS send.");
			// In development, log the OTP instead of failing
			log.info("[DEV MODE] Would send SMS to {}: {}", phoneNumber, message);
			return true;
		}

		try {
			RestTemplate restTemplate = new RestTemplate();
			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("apikey", smsProperties.getSemaphore()
				.getApiKey());
			params.add("number", phoneNumber);
			params.add("message", message);
			params.add("sendername", smsProperties.getSemaphore()
				.getSenderName());

			log.info("[PLACEHOLDER] Sending SMS via Semaphore to {}: {}", phoneNumber, message);

			ResponseEntity<String> response = restTemplate.postForEntity(
				"https://semaphore.co/api/v4/messages",
				params,
				String.class
			);

			if (response.getStatusCode()
				.is2xxSuccessful()) {
				log.info("SMS sent successfully via Semaphore to {}: {}", phoneNumber, message);
				return true;
			}

		} catch (Exception e) {
			log.error("Failed to send SMS via Semaphore to {}: {}", phoneNumber, e.getMessage(), e);
			return false;
		}

		return false;
	}

	@Override
	public String getProviderName() {
		return PROVIDER_NAME;
	}

	@Override
	public boolean isConfigured() {
		return smsProperties.getSemaphore() != null
			&& smsProperties.getSemaphore()
			.getApiKey() != null
			&& !smsProperties.getSemaphore()
			.getApiKey()
			.isBlank()
			&& !smsProperties.getSemaphore()
			.getApiKey()
			.equals("your-api-key");
	}
}

