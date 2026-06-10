package com.ria.olita.tech.silingan.util;

import com.ria.olita.tech.silingan.config.KeycloakProperties;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class KeycloakTokenUtil {

    private final KeycloakProperties keycloakProperties;


    /**
     * Get access token using Password Grant (Resource Owner).
     * This is suitable for user login scenarios.
     * 
     * @param username The user's username
     * @param password The user's password
     */
    public String getAccessToken(String username, String password) {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(keycloakProperties.getUrl())
                .realm(keycloakProperties.getRealm())
                .clientId(keycloakProperties.getClientId())
                .clientSecret(keycloakProperties.getClientSecret())
                .grantType(OAuth2Constants.PASSWORD)
                .username(username)
                .password(password)
                .build();

        AccessTokenResponse tokenResponse = keycloak.tokenManager().getAccessToken();
        return tokenResponse.getToken();
    }



    /**
     * Build Keycloak client for Client Credentials Grant.
     */
    private Keycloak getKeycloakClient() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakProperties.getUrl())
                .realm(keycloakProperties.getRealm())
                .clientId(keycloakProperties.getClientId())
                .clientSecret(keycloakProperties.getClientSecret())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }

    /**
     * Get the token endpoint URL.
     */
    public String getTokenEndpoint() {
        return String.format("%s/realms/%s/protocol/openid-connect/token",
                keycloakProperties.getUrl(),
                keycloakProperties.getRealm());
    }
}
