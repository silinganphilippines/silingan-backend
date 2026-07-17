package com.ria.olita.tech.silingan.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import com.ria.olita.tech.silingan.dto.req.CreateUserRequest;
import com.ria.olita.tech.silingan.dto.req.RegistrationProofTokenRequest;
import com.ria.olita.tech.silingan.dto.res.AuthTokenResponse;
import com.ria.olita.tech.silingan.entity.SilinganRealmRole;
import com.ria.olita.tech.silingan.service.UserService;
import com.ria.olita.tech.silingan.service.auth.RegistrationAuthProofService;
import com.ria.olita.tech.silingan.service.auth.RegistrationTokenExchangeService;

class AuthControllerTest {

    @Test
    void shouldReturnRegistrationAuthProofAfterSelfServiceRegister() {
        UserService userService = Mockito.mock(UserService.class);
        RegistrationAuthProofService proofService = Mockito.mock(RegistrationAuthProofService.class);
        RegistrationTokenExchangeService tokenService = Mockito.mock(RegistrationTokenExchangeService.class);

        when(proofService.issueProof("jdelacruz", "SecurePass123!")).thenReturn("proof-123");

        AuthController controller = new AuthController(userService, proofService, tokenService);

        CreateUserRequest request = CreateUserRequest.builder()
                .username("jdelacruz")
                .email("juan@example.com")
                .firstName("Juan")
                .lastName("Dela Cruz")
                .password("SecurePass123!")
                .mobileNumber("+639171234567")
                .communityRole(SilinganRealmRole.RESIDENT)
                .communityId(UUID.randomUUID())
                .build();

        ResponseEntity<Map<String, Object>> response = controller.selfServiceRegister(request);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody()).containsEntry("registrationAuthProof", "proof-123");
        verify(userService).createSelfServiceUser(request);
    }

    @Test
    void shouldExchangeTokenByRegistrationProof() {
        UserService userService = Mockito.mock(UserService.class);
        RegistrationAuthProofService proofService = Mockito.mock(RegistrationAuthProofService.class);
        RegistrationTokenExchangeService tokenService = Mockito.mock(RegistrationTokenExchangeService.class);

        AuthTokenResponse tokenResponse = new AuthTokenResponse("access", "refresh", 300L, 1800L, "Bearer");
        when(tokenService.exchangeByRegistrationProof("proof-123")).thenReturn(tokenResponse);

        AuthController controller = new AuthController(userService, proofService, tokenService);

        ResponseEntity<AuthTokenResponse> response =
                controller.tokenByRegistrationProof(new RegistrationProofTokenRequest("proof-123"));

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(tokenResponse);
    }
}

