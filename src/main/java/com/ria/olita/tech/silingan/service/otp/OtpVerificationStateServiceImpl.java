package com.ria.olita.tech.silingan.service.otp;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ria.olita.tech.silingan.entity.OtpVerificationState;
import com.ria.olita.tech.silingan.repository.OtpVerificationStateRepository;

import java.time.Instant;
import java.util.Objects;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpVerificationStateServiceImpl implements OtpVerificationStateService {

	private final OtpVerificationStateRepository otpVerificationStateRepository;

	@Override
	@Transactional(readOnly = true)
	public boolean isVerified(String keycloakUserId, String tokenId) {
		return otpVerificationStateRepository
			.findByKeycloakUserIdAndOtpVerifiedTrueAndExpiresAtAfter(keycloakUserId, Instant.now())
			.stream()
			.anyMatch(state -> Objects.equals(tokenId, state.getTokenId()));
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isVerifiedForUser(String keycloakUserId) {
		return otpVerificationStateRepository.existsByKeycloakUserIdAndOtpVerifiedTrueAndExpiresAtAfter(
			keycloakUserId,
			Instant.now()
		);
	}

	@Override
	@Transactional
	public void markVerified(String keycloakUserId, String tokenId, Instant expiresAt) {
		clearVerification(keycloakUserId, tokenId);

		OtpVerificationState state = OtpVerificationState.builder()
			.keycloakUserId(keycloakUserId)
			.tokenId(tokenId)
			.otpVerified(true)
			.verifiedAt(Instant.now())
			.expiresAt(expiresAt)
			.build();

		otpVerificationStateRepository.save(state);
	}

	@Override
	@Transactional
	public void clearVerification(String keycloakUserId, String tokenId) {
		if (tokenId == null || tokenId.isBlank()) {
			otpVerificationStateRepository.deleteByKeycloakUserIdAndTokenIdIsNull(keycloakUserId);
			return;
		}
		otpVerificationStateRepository.deleteByKeycloakUserIdAndTokenId(keycloakUserId, tokenId);
	}
}

