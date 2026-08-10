package com.ria.olita.tech.silingan.service.otp;

import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegistrationOtpProofServiceImpl implements RegistrationOtpProofService {

	private final Cache<String, Boolean> registrationOtpProofCache;

	@Override
	public void markVerifiedForRegistration(String mobileNumber) {
		String normalized = normalizePhoneNumber(mobileNumber);
		if (normalized == null || normalized.isBlank()) {
			return;
		}
		registrationOtpProofCache.put(normalized, Boolean.TRUE);
	}

	@Override
	public boolean consumeRegistrationProof(String mobileNumber) {
		String normalized = normalizePhoneNumber(mobileNumber);
		if (normalized == null || normalized.isBlank()) {
			return false;
		}
		Boolean verified = registrationOtpProofCache.getIfPresent(normalized);
		if (Boolean.TRUE.equals(verified)) {
			registrationOtpProofCache.invalidate(normalized);
			return true;
		}
		return false;
	}

	private String normalizePhoneNumber(String phoneNumber) {
		if (phoneNumber == null) {
			return null;
		}

		String cleaned = phoneNumber.trim();
		if (cleaned.isBlank()) {
			return null;
		}

		if (cleaned.startsWith("0")) {
			return "+63" + cleaned.substring(1);
		}

		if (!cleaned.startsWith("+")) {
			return "+" + cleaned;
		}

		return cleaned;
	}
}

