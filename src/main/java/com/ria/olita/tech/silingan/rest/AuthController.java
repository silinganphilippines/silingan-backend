package com.ria.olita.tech.silingan.rest;

import com.ria.olita.tech.silingan.dto.req.CreateUserRequest;
import com.ria.olita.tech.silingan.dto.req.OtpVerifyRequest;
import com.ria.olita.tech.silingan.dto.res.LoginResponse;
import com.ria.olita.tech.silingan.service.UserService;
import com.ria.olita.tech.silingan.service.auth.AuthenticationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication and user registration APIs")
public class AuthController {

	private static final Logger log = LoggerFactory.getLogger(AuthController.class);

	private final UserService userService;
	private final AuthenticationService authenticationService;

	@PostMapping("/register")
	@PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('COMMUNITY_ADMIN')")
	@Operation(
		summary = "Register a new user",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
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
						        	"mobileNumber": "+639171234567",
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
		log.debug("Registration request details: {}", request);

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
			log.error("Registration failed for user {}: {}", request.username(), e.getMessage());
			log.error("Stack trace: ", e);
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("message", e.getMessage() != null ? e.getMessage() : "An unexpected error occurred");
			errorResponse.put("errorType", e.getClass()
				.getSimpleName());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(errorResponse);
		}
	}

	@PostMapping("/register/self-service")
	@Operation(summary = "Self-service user registration", description = "Registers a user after OTP verification of mobile number")
	public ResponseEntity<Map<String, Object>> selfServiceRegister(
		@Valid @RequestBody CreateUserRequest request) {
		log.info("Self-service registration request received for username: {}", request.username());

		try {
			userService.createSelfServiceUser(request);

			Map<String, Object> payload = new HashMap<>();
			payload.put("success", true);
			payload.put("message", "User registered successfully");
			payload.put("username", request.username());

			LoginResponse response = authenticationService.issueTokenForMobile(request.mobileNumber(),request.communityId());
			payload.put("login", response);
			payload.put("accessToken", response.accessToken());
			payload.put("tokenType", response.tokenType());
			payload.put("expiresIn", response.expiresIn());
			payload.put("roles", response.roles());

			return ResponseEntity.status(HttpStatus.CREATED)
				.body(payload);
		} catch (Exception e) {
			log.error("Self-service registration failed for user {}: {}", request.username(), e.getMessage());
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("message", e.getMessage() != null ? e.getMessage() : "An unexpected error occurred");
			errorResponse.put("errorType", e.getClass().getSimpleName());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(errorResponse);
		}
	}


	@PostMapping("/login/otp")
	@Operation(summary = "Login with OTP", description = "Verifies OTP and returns backend-issued JWT")
	public ResponseEntity<LoginResponse> loginWithOtp(
		@Valid @RequestBody OtpVerifyRequest request,
		HttpServletRequest httpRequest) {
		return ResponseEntity.ok(authenticationService.loginWithOtp(
			request,
			getClientIpAddress(httpRequest),
			httpRequest.getHeader("User-Agent")
		));
	}

	private String getClientIpAddress(HttpServletRequest request) {
		String xForwardedFor = request.getHeader("X-Forwarded-For");
		if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
			return xForwardedFor.split(",")[0].trim();
		}
		String xRealIp = request.getHeader("X-Real-IP");
		if (xRealIp != null && !xRealIp.isEmpty()) {
			return xRealIp;
		}
		return request.getRemoteAddr();
	}
}
