package com.ria.olita.tech.silingan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ria.olita.tech.silingan.entity.OtpVerificationState;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface OtpVerificationStateRepository extends JpaRepository<OtpVerificationState, UUID> {

	List<OtpVerificationState> findByKeycloakUserIdAndOtpVerifiedTrueAndExpiresAtAfter(
		String keycloakUserId,
		Instant now
	);

	void deleteByKeycloakUserIdAndTokenId(String keycloakUserId, String tokenId);

	void deleteByKeycloakUserIdAndTokenIdIsNull(String keycloakUserId);
}

