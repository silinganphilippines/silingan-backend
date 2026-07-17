package com.ria.olita.tech.silingan.dto.res;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthTokenResponse(
        String accessToken,
        String refreshToken,
        Long expiresIn,
        Long refreshExpiresIn,
        String tokenType
) {
}

