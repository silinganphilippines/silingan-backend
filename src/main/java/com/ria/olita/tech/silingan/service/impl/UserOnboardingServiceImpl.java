package com.ria.olita.tech.silingan.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.ria.olita.tech.silingan.dto.req.RegisterRequest;
import com.ria.olita.tech.silingan.entity.Community;
import com.ria.olita.tech.silingan.entity.CommunityRole;
import com.ria.olita.tech.silingan.entity.User;
import com.ria.olita.tech.silingan.entity.UserCommunity;
import com.ria.olita.tech.silingan.repository.CommunityRepository;
import com.ria.olita.tech.silingan.repository.UserRepository;
import com.ria.olita.tech.silingan.service.KeycloakService;
import com.ria.olita.tech.silingan.service.UserOnboardingService;
import com.ria.olita.tech.silingan.dto.req.CreateUserRequest;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserOnboardingServiceImpl implements UserOnboardingService {

	private static final Logger log = LoggerFactory.getLogger(UserOnboardingServiceImpl.class);

	private final KeycloakService keycloakService;
	private final UserRepository userRepository;
	private final CommunityRepository communityRepository;

	@Override
	@Transactional
	public void registerUser(RegisterRequest request) {
		log.info("Registering user: {}", request.username());

		// Split full name into first and last name
		String[] nameParts = splitFullName(request.fullName());
		String firstName = nameParts[0];
		String lastName = nameParts.length > 1 ? nameParts[1] : "";

		Community community = communityRepository.findByCode(request.communityCode())
			.orElseThrow(() -> new RuntimeException("Community not found with code: " + request.communityCode()));

		// Prepare attributes
		Map<String, List<String>> attributes = new HashMap<>();
		if (StringUtils.hasText(request.contactNumber())) {
			attributes.put("contactNumber", List.of(request.contactNumber()));
		}
		attributes.put("communityId", List.of(community.getId()
			.toString()));

		// Create Keycloak user request using builder
		CreateUserRequest createUserRequest = CreateUserRequest.builder()
			.username(request.username())
			.email(request.email())
			.firstName(firstName)
			.lastName(lastName)
			.password(request.password())
			.enabled(true)
			.emailVerified(false)
			.communityCode(request.communityCode())
			.attributes(attributes)
			.build();

		// Create user in Keycloak
		String keycloakUserId = keycloakService.createUser(createUserRequest);

		// Create user in database
		User user = User.builder()
			.keycloakUserId(keycloakUserId)
			.build();

		userRepository.save(user);
		log.info("User saved to database with ID: {}", user.getId());

		// Create UserCommunity if community code is provided
		if (request.communityCode() != null && !request.communityCode()
			.isEmpty()) {

			UserCommunity userCommunity = new UserCommunity();
			userCommunity.setUser(user);
			userCommunity.setCommunity(community);
			userCommunity.setRole(CommunityRole.RESIDENT);

			if (Objects.isNull(user.getCommunities())) {
				List<UserCommunity> communities = new ArrayList<>();
				communities.add(userCommunity);
				user.setCommunities(communities);
			} else {
				user.getCommunities()
					.add(userCommunity);
			}
			userRepository.save(user);
			log.info("UserCommunity created for user {} in community {}", user.getId(), community.getId());
		}
	}

	private String[] splitFullName(String fullName) {
		if (fullName == null || fullName.trim()
			.isEmpty()) {
			return new String[]{"", ""};
		}
		return fullName.trim()
			.split("\\s+");
	}
}
