package com.ria.olita.tech.silingan.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Configuration
@ConfigurationProperties(prefix = "keycloak")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeycloakProperties {
	private String url;
	private String realm;
	private String clientId;
	private String clientSecret;
}
