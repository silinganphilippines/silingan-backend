package com.ria.olita.tech.silingan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "otp_verification_state", indexes = {
	@Index(name = "idx_otp_state_user", columnList = "keycloak_user_id"),
	@Index(name = "idx_otp_state_exp", columnList = "expires_at"),
	@Index(name = "idx_otp_state_user_token", columnList = "keycloak_user_id,token_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpVerificationState {

	@Id
	@GeneratedValue
	private UUID id;

	@Column(name = "keycloak_user_id", nullable = false, length = 128)
	private String keycloakUserId;

	@Column(name = "token_id", length = 256)
	private String tokenId;

	@Column(name = "otp_verified", nullable = false)
	private boolean otpVerified;

	@Column(name = "verified_at", nullable = false)
	private Instant verifiedAt;

	@Column(name = "expires_at", nullable = false)
	private Instant expiresAt;
}

