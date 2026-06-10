package com.ria.olita.tech.silingan.service.impl;

import org.springframework.stereotype.Service;

import com.ria.olita.tech.silingan.dto.req.CreateUserRequest;
import com.ria.olita.tech.silingan.entity.User;
import com.ria.olita.tech.silingan.repository.UserRepository;
import com.ria.olita.tech.silingan.service.KeycloakService;
import com.ria.olita.tech.silingan.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final KeycloakService keycloakService;
	private final UserRepository userRepository;

	@Override
	public void createUser(CreateUserRequest request) {
		log.info("Registering user in kc and in db: {}", request.username());

		// Create user in Keycloak
		String keycloakUserId = keycloakService.createUser(request);
		log.info("User saved to kc with ID: {}", keycloakUserId);

		// Create user in database
		User user = User.builder()
			.keycloakUserId(keycloakUserId)
			.build();

		userRepository.save(user);
		log.info("User saved to database with ID: {}", user.getId());
	}
}




