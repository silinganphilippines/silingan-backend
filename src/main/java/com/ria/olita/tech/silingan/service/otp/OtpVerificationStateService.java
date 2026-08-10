package com.ria.olita.tech.silingan.service.otp;

import java.time.Instant;

public interface OtpVerificationStateService {

	boolean isVerified(String keycloakUserId, String tokenId);

	boolean isVerifiedForUser(String keycloakUserId);

	void markVerified(String keycloakUserId, String tokenId, Instant expiresAt);

	void clearVerification(String keycloakUserId, String tokenId);
}

