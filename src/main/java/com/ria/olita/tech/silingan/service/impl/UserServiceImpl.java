package com.ria.olita.tech.silingan.service.impl;

import org.springframework.stereotype.Service;

import com.ria.olita.tech.silingan.dto.req.CreateUserRequest;
import com.ria.olita.tech.silingan.entity.Community;
import com.ria.olita.tech.silingan.entity.User;
import com.ria.olita.tech.silingan.entity.UserCommunity;
import com.ria.olita.tech.silingan.exception.NotFoundException;
import com.ria.olita.tech.silingan.repository.CommunityRepository;
import com.ria.olita.tech.silingan.repository.UserRepository;
import com.ria.olita.tech.silingan.service.KeycloakService;
import com.ria.olita.tech.silingan.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
	private final KeycloakService keycloakService;
	private final UserRepository userRepository;
	private final CommunityRepository communityRepository;

	@Override
	public void createUser(CreateUserRequest request) {
		log.info("Registering user in kc and in db: {}", request.username());

		Community community = communityRepository
			.findById(request.communityId())
			.orElseThrow(() -> new NotFoundException("Community not found with id = "+ request.communityId()));

		//1. Create user in Keycloak
		String keycloakUserId = keycloakService.createUser(request);
		log.info("User saved to kc with ID: {}", keycloakUserId);

		//2. Create user in database
		User user = User.builder()
			.keycloakUserId(keycloakUserId)
			.username(request.username())
			.email(request.email())
			.firstName(request.firstName())
			.lastName(request.lastName())
			.build();

		//3. Link user to community with role
		UserCommunity userCommunity = UserCommunity.builder()
			.user(user)
			.community(community)
			.role(request.communityRole())
			.build();

		user.addCommunity(userCommunity);

		userRepository.save(user);
		log.info("User saved to database with ID: {}", user.getId());
	}
}




