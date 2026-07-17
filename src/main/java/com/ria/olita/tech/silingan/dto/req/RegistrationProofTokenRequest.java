package com.ria.olita.tech.silingan.dto.req;

import jakarta.validation.constraints.NotBlank;

public record RegistrationProofTokenRequest(
        @NotBlank(message = "registrationAuthProof is required")
        String registrationAuthProof
) {
}

