package com.ria.olita.tech.silingan.rest;

import com.ria.olita.tech.silingan.dto.req.CreateUserRequest;
import com.ria.olita.tech.silingan.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication and user registration APIs")
public class AuthController {

	private static final Logger log = LoggerFactory.getLogger(AuthController.class);

	private final UserService userService;

	@PostMapping("/register")
	@PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('COMMUNITY_ADMIN')")
	@Operation(
		summary = "Register a new user",
		requestBody = @RequestBody(
			required = true,
			content = @Content(
				schema = @Schema(implementation = CreateUserRequest.class),
				examples = {
					@ExampleObject(
						name = "registerUserExample",
						summary = "Example user registration",
						value = """
							{
								"username": "jdelacruz",
								"email": "juan@example.com",
								"firstName": "Juan",
								"lastName": "Dela Cruz",
								"password": "SecurePass123!",
								"enabled": true,
								"emailVerified": true,
								"communityRole": "RESIDENT",
								"communityId": "550e8400-e29b-41d4-a716-446655440000",
								"address": {
									"street": "123 Main St",
									"barangay": "Poblacion",
									"city": "Makati",
									"province": "Metro Manila",
									"region": 13,
									"postalCode": "1200",
									"country": "Philippines"
								}
							}
							"""
					)
				}
			)
		)
	)
	public ResponseEntity<Map<String, Object>> register(
		@Valid @RequestBody CreateUserRequest request) {
		log.info("Registration request received for username: {}", request.username());

		try {
			// Register user in Keycloak and database
			userService.createUser(request);

			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("message", "User registered successfully");
			response.put("username", request.username());

			return ResponseEntity.status(HttpStatus.CREATED)
				.body(response);

		} catch (Exception e) {
			log.error("Registration failed: ", e);
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(errorResponse);
		}
	}

}
