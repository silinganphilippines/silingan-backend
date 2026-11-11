package com.ria.olita.tech.silingan.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

	@NotBlank
	private String secret;

	@NotBlank
	private String issuer = "silingan-backend";

	@Min(60)
	private long accessTokenTtlSeconds = 3600;
}

