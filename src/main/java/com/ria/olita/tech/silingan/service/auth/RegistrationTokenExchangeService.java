package com.ria.olita.tech.silingan.service.auth;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ria.olita.tech.silingan.config.KeycloakProperties;
import com.ria.olita.tech.silingan.dto.res.AuthTokenResponse;
import com.ria.olita.tech.silingan.exception.UnauthorizedException;
import com.ria.olita.tech.silingan.service.auth.RegistrationAuthProofService.RegistrationCredentials;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegistrationTokenExchangeService {

    private static final Logger log = LoggerFactory.getLogger(RegistrationTokenExchangeService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final RegistrationAuthProofService registrationAuthProofService;
    private final KeycloakProperties keycloakProperties;

    public AuthTokenResponse exchangeByRegistrationProof(String registrationAuthProof) {
        RegistrationCredentials credentials = registrationAuthProofService.getProof(registrationAuthProof);
        if (credentials == null) {
            throw new UnauthorizedException("Invalid or expired registration auth proof");
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("grant_type", "password");
            String clientId = keycloakProperties.getTokenClientId() != null 
                ? keycloakProperties.getTokenClientId() 
                : keycloakProperties.getClientId();
            String clientSecret = keycloakProperties.getTokenClientSecret() != null 
                ? keycloakProperties.getTokenClientSecret() 
                : keycloakProperties.getClientSecret();
            form.add("client_id", clientId);
            form.add("client_secret", clientSecret);
            form.add("username", credentials.username());
            form.add("password", credentials.password());

            log.debug("Requesting token from Keycloak endpoint: {} for user: {} with client_id: {}",
                    tokenEndpoint(), credentials.username(), clientId);

            ResponseEntity<?> response = restTemplate.postForEntity(
                    tokenEndpoint(),
                    new HttpEntity<>(form, headers),
                    Map.class);

            log.debug("Keycloak token response status: {}", response.getStatusCode());

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                String errorMsg = "Keycloak token request failed with status: " + response.getStatusCode();
                Object body = response.getBody();
                
                if (body instanceof Map<?, ?> errorBody) {
                    String error = (String) errorBody.get("error");
                    String description = (String) errorBody.get("error_description");
                    errorMsg += " | error: " + error + " | description: " + description;
                    
                    if ("unauthorized_client".equals(error)) {
                        log.error("{} [HINT: Enable 'Direct Access Grants Allowed' in Keycloak client settings for '{}']",
                            errorMsg, keycloakProperties.getClientId());
                    } else {
                        log.error(errorMsg);
                    }
                } else {
                    log.error("{} | body: {}", errorMsg, body);
                }
                
                throw new UnauthorizedException("Unable to issue access token");
            }

            AuthTokenResponse tokenResponse = toTokenResponse(castPayload(response.getBody()));
            registrationAuthProofService.consumeProof(registrationAuthProof);
            log.debug("Token exchange successful, registration proof consumed");
            return tokenResponse;
        } catch (HttpClientErrorException ex) {
            String errorMsg = "Keycloak token request failed with HTTP " + ex.getStatusCode();
            String responseBody = ex.getResponseBodyAsString();
            
            log.debug("Keycloak error response body: {}", responseBody);
            
            if (responseBody != null && !responseBody.isEmpty()) {
                try {
                    // Try to parse the response body as JSON to extract error details
                    Map<?, ?> errorBody = objectMapper.readValue(responseBody, Map.class);
                    String error = (String) errorBody.get("error");
                    String description = (String) errorBody.get("error_description");
                    errorMsg += " | error: " + error + " | description: " + description;
                    
                    if ("unauthorized_client".equals(error)) {
                        log.error("{} [HINT: Enable 'Direct Access Grants Allowed' in Keycloak client settings and verify client credentials]",
                            errorMsg);
                    } else if (ex.getStatusCode().value() == 401) {
                        log.error("{} [HINT: Verify Keycloak client credentials (client_id and client_secret) are correct. " +
                                 "Ensure the client is properly configured in Keycloak.]", errorMsg);
                    } else {
                        log.error(errorMsg);
                    }
                } catch (Exception parseEx) {
                    log.error("{} | raw response: {}", errorMsg, responseBody);
                }
            } else {
                if (ex.getStatusCode().value() == 401) {
                    log.error("{} [HINT: Verify Keycloak client credentials (client_id and client_secret) are correct. " +
                             "Ensure the client is properly configured in Keycloak.]", errorMsg);
                } else {
                    log.error(errorMsg);
                }
            }
            log.error("Registration-proof token exchange failed with HttpClientErrorException: {}", ex.getMessage(), ex);
            throw new UnauthorizedException("Unable to issue access token");
        } catch (RestClientException ex) {
            log.error("Registration-proof token exchange failed with RestClientException: {}", ex.getMessage(), ex);
            throw new UnauthorizedException("Unable to issue access token");
        }
    }

    private String tokenEndpoint() {
        String baseUrl = keycloakProperties.getUrl();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl + "/realms/" + keycloakProperties.getRealm() + "/protocol/openid-connect/token";
    }

    private AuthTokenResponse toTokenResponse(Map<String, Object> payload) {
        return new AuthTokenResponse(
                asString(payload.get("access_token")),
                asString(payload.get("refresh_token")),
                asLong(payload.get("expires_in")),
                asLong(payload.get("refresh_expires_in")),
                asString(payload.get("token_type")));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> castPayload(Object payload) {
        if (!(payload instanceof Map<?, ?> map)) {
            throw new UnauthorizedException("Unable to issue access token");
        }
        return (Map<String, Object>) map;
    }

    private String asString(Object value) {
        return value == null ? null : value.toString();
    }

    private Long asLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value == null) {
            return null;
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}



