package com.ria.olita.tech.silingan.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Home controller providing welcome endpoint.
 * Requires authentication via Keycloak OAuth2.
 *
 * @author RIA OLITA
 */
@RestController
@RequestMapping("/")
public class HomeController {

    /**
     * Welcome home endpoint.
     * Requires authentication - will redirect to Keycloak login if not authenticated.
     *
     * @param jwt The authenticated JWT token from Keycloak
     * @return Welcome message with user information
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> home(@AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> response = new HashMap<>();
        
        response.put("message", "Welcome to Silingan!");
        response.put("status", "success");
        
        if (jwt != null) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("subject", jwt.getSubject());
            userInfo.put("email", jwt.getClaimAsString("email"));
            userInfo.put("name", jwt.getClaimAsString("name"));
            userInfo.put("preferredUsername", jwt.getClaimAsString("preferred_username"));
            response.put("user", userInfo);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Alternative home endpoint at /home path.
     * Also requires authentication via Keycloak.
     *
     * @param jwt The authenticated JWT token from Keycloak
     * @return Welcome message with user information
     */
    @GetMapping("/home")
    public ResponseEntity<Map<String, Object>> welcome(@AuthenticationPrincipal Jwt jwt) {
        return home(jwt);
    }
}
