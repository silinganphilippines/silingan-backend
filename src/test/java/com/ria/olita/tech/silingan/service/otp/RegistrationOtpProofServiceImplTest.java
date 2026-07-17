package com.ria.olita.tech.silingan.service.otp;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

class RegistrationOtpProofServiceImplTest {

	@Test
	void shouldConsumeProofOnce() {
		Cache<String, Boolean> cache = Caffeine.newBuilder().maximumSize(100).build();
		RegistrationOtpProofServiceImpl service = new RegistrationOtpProofServiceImpl(cache);

		service.markVerifiedForRegistration("09171234567");

		assertThat(service.consumeRegistrationProof("+639171234567")).isTrue();
		assertThat(service.consumeRegistrationProof("+639171234567")).isFalse();
	}

	@Test
	void shouldReturnFalseWhenNoProofExists() {
		Cache<String, Boolean> cache = Caffeine.newBuilder().maximumSize(100).build();
		RegistrationOtpProofServiceImpl service = new RegistrationOtpProofServiceImpl(cache);

		assertThat(service.consumeRegistrationProof("+639171234567")).isFalse();
	}
}

