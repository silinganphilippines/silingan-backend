package com.ria.olita.tech.silingan.service.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.ria.olita.tech.silingan.config.OtpProperties;
import com.ria.olita.tech.silingan.exception.ValidationException;
import com.ria.olita.tech.silingan.service.auth.RegistrationAuthProofService.RegistrationCredentials;

class RegistrationAuthProofServiceTest {

    @Test
    void shouldIssueAndConsumeProofOnce() {
        RegistrationAuthProofService service = new RegistrationAuthProofService(new OtpProperties());

        String proof = service.issueProof("jdelacruz", "SecurePass123!");

        RegistrationCredentials firstConsume = service.consumeProof(proof);
        RegistrationCredentials secondConsume = service.consumeProof(proof);

        assertThat(firstConsume).isNotNull();
        assertThat(firstConsume.username()).isEqualTo("jdelacruz");
        assertThat(secondConsume).isNull();
    }

    @Test
    void shouldRejectIssueWhenPasswordMissing() {
        RegistrationAuthProofService service = new RegistrationAuthProofService(new OtpProperties());

        assertThrows(ValidationException.class, () -> service.issueProof("jdelacruz", ""));
    }
}

