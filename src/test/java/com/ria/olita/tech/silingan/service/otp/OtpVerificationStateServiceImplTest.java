package com.ria.olita.tech.silingan.service.otp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.ria.olita.tech.silingan.entity.OtpVerificationState;
import com.ria.olita.tech.silingan.repository.OtpVerificationStateRepository;

import java.time.Instant;
import java.util.List;

class OtpVerificationStateServiceImplTest {

	@Test
	void shouldReturnTrueWhenMatchingTokenExists() {
		OtpVerificationStateRepository repository = Mockito.mock(OtpVerificationStateRepository.class);
		OtpVerificationStateServiceImpl service = new OtpVerificationStateServiceImpl(repository);

		when(repository.findByKeycloakUserIdAndOtpVerifiedTrueAndExpiresAtAfter(eq("user-1"), any(Instant.class)))
			.thenReturn(List.of(OtpVerificationState.builder().keycloakUserId("user-1").tokenId("jti-1").otpVerified(true).expiresAt(Instant.now().plusSeconds(30)).build()));

		assertThat(service.isVerified("user-1", "jti-1")).isTrue();
	}

	@Test
	void shouldPersistVerifiedState() {
		OtpVerificationStateRepository repository = Mockito.mock(OtpVerificationStateRepository.class);
		OtpVerificationStateServiceImpl service = new OtpVerificationStateServiceImpl(repository);

		service.markVerified("user-1", "jti-1", Instant.now().plusSeconds(60));

		verify(repository).deleteByKeycloakUserIdAndTokenId("user-1", "jti-1");
		ArgumentCaptor<OtpVerificationState> captor = ArgumentCaptor.forClass(OtpVerificationState.class);
		verify(repository).save(captor.capture());
		assertThat(captor.getValue().getKeycloakUserId()).isEqualTo("user-1");
		assertThat(captor.getValue().getTokenId()).isEqualTo("jti-1");
		assertThat(captor.getValue().isOtpVerified()).isTrue();
	}

	@Test
	void shouldClearNullTokenUsingNullBranch() {
		OtpVerificationStateRepository repository = Mockito.mock(OtpVerificationStateRepository.class);
		OtpVerificationStateServiceImpl service = new OtpVerificationStateServiceImpl(repository);

		service.clearVerification("user-1", null);

		verify(repository).deleteByKeycloakUserIdAndTokenIdIsNull("user-1");
		verify(repository, never()).deleteByKeycloakUserIdAndTokenId(any(), any());
	}
}
