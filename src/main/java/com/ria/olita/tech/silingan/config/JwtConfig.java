package com.ria.olita.tech.silingan.config;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

@Configuration
public class JwtConfig {

	@Bean
	public SecretKey jwtSecretKey(JwtProperties jwtProperties) {
		return new SecretKeySpec(jwtProperties.getSecret().getBytes(), "HmacSHA256");
	}

	@Bean
	public JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {
		return new NimbusJwtEncoder(new ImmutableSecret<>(jwtSecretKey));
	}

	JwtDecoder backendJwtDecoder(SecretKey jwtSecretKey, JwtProperties jwtProperties) {
		NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(jwtSecretKey)
			.macAlgorithm(MacAlgorithm.HS256)
			.build();
		decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(jwtProperties.getIssuer()));
		return decoder;
	}

	JwtDecoder keycloakJwtDecoder(KeycloakProperties keycloakProperties) {
		String jwkSetUri = buildKeycloakJwkSetUri(keycloakProperties);
		String issuer = buildKeycloakIssuer(keycloakProperties);

		NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
		decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));
		return decoder;
	}

	@Bean
	public JwtDecoder jwtDecoder(SecretKey jwtSecretKey,
	                             JwtProperties jwtProperties,
	                             KeycloakProperties keycloakProperties) {
		JwtDecoder backendDecoder = backendJwtDecoder(jwtSecretKey, jwtProperties);
		JwtDecoder keycloakDecoder = keycloakJwtDecoder(keycloakProperties);

		return token -> {
			try {
				return backendDecoder.decode(token);
			} catch (JwtException backendEx) {
				try {
					return keycloakDecoder.decode(token);
				} catch (JwtException keycloakEx) {
					throw new BadJwtException("Token validation failed for both backend and Keycloak decoders", keycloakEx);
				}
			}
		};
	}

	private String buildKeycloakJwkSetUri(KeycloakProperties keycloakProperties) {
		String baseUrl = trimTrailingSlash(keycloakProperties.getUrl());
		return baseUrl + "/realms/" + keycloakProperties.getRealm() + "/protocol/openid-connect/certs";
	}

	private String buildKeycloakIssuer(KeycloakProperties keycloakProperties) {
		String baseUrl = trimTrailingSlash(keycloakProperties.getUrl());
		return baseUrl + "/realms/" + keycloakProperties.getRealm();
	}

	private String trimTrailingSlash(String value) {
		if (value == null || value.isBlank()) {
			return value;
		}
		if (value.endsWith("/")) {
			return value.substring(0, value.length() - 1);
		}
		return value;
	}
}


