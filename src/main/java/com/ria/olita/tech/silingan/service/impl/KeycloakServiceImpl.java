package com.ria.olita.tech.silingan.service.impl;

import com.ria.olita.tech.silingan.config.KeycloakProperties;
import com.ria.olita.tech.silingan.dto.req.CreateUserRequest;
import com.ria.olita.tech.silingan.entity.CommunityRole;
import com.ria.olita.tech.silingan.service.KeycloakService;

import jakarta.ws.rs.core.Response;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KeycloakServiceImpl implements KeycloakService {

	private static final Logger log = LoggerFactory.getLogger(KeycloakServiceImpl.class);

	@Autowired
	private KeycloakProperties keycloakProperties;

	@Override
	public String createUser(CreateUserRequest request) {
		log.info("Creating user in Keycloak: {}", request.firstName());

		Keycloak keycloak = getKeycloakClient();
		RealmResource realmResource = keycloak.realm(keycloakProperties.getRealm());
		UsersResource usersResource = realmResource.users();

		// Create user representation
		UserRepresentation user = new UserRepresentation();
		user.setUsername(request.username());
		user.setEmailVerified(Boolean.TRUE);
		user.setEmail(request.email());
		user.setFirstName(request.firstName());
		user.setLastName(request.lastName());
		user.setEnabled(request.enabled());
		user.setCredentials(Collections.singletonList(createPasswordCredential(request.password())));
		user.setRequiredActions(Collections.emptyList());
		// Assign realm role after user creation
		// Note: Realm roles must be assigned after user is created
		if (request.attributes() != null && !request.attributes()
			.isEmpty()) {
			user.setAttributes(request.attributes());
		}

		// Create the user
		Response response = usersResource.create(user);

		if (response.getStatus() == 201) {
			String userId = extractUserId(response);
			log.info("User created successfully with ID: {}", userId);

			if (request.attributes() != null && !request.attributes()
				.isEmpty()) {
				updateUserAttributes(userId, request.attributes());
			}

			// Add user to community group if communityCode is provided
			if (request.communityCode() != null && !request.communityCode()
				.isEmpty()) {
				addUserToCommunityGroup(realmResource, userId, request.communityCode());
			}

			// Assign default RESIDENT role to the user
			assignRealmRole(realmResource, userId, CommunityRole.RESIDENT.name());

			return userId;
		} else if (response.getStatus() == 409) {
			log.error("User already exists: {}", request.username());
			throw new RuntimeException("User already exists: " + request.firstName());
		} else {
			String errorMessage = "Failed to create user. Status: " + response.getStatus();
			log.error(errorMessage);
			throw new RuntimeException(errorMessage);
		}
	}

	private void addUserToCommunityGroup(RealmResource realmResource, String userId, String communityCode) {
		log.info("Adding user {} to community group: {}", userId, communityCode);

		try {
			GroupsResource groupsResource = realmResource.groups();

			List<GroupRepresentation> groups = groupsResource.groups(communityCode, 0, 1, true);

			if (groups == null || groups.isEmpty()) {
				throw new RuntimeException("Group not found for community code: " + communityCode);
			}

			String groupId = groups.get(0)
				.getId();

			log.info("Found group {} for communityCode {}", groupId, communityCode);

			UserResource userResource = realmResource.users()
				.get(userId);

			userResource.joinGroup(groupId);

			log.info("User {} successfully added to group {}", userId, communityCode);

		} catch (Exception e) {
			log.error("Error adding user to community group: {}", e.getMessage(), e);
			throw new RuntimeException("Failed to add user to community group: " + communityCode, e);
		}
	}

	private Keycloak getKeycloakClient() {
		return KeycloakBuilder.builder()
			.serverUrl(keycloakProperties.getUrl())
			.realm(keycloakProperties.getRealm())
			.clientId(keycloakProperties.getClientId())
			.clientSecret(keycloakProperties.getClientSecret())
			.grantType(OAuth2Constants.CLIENT_CREDENTIALS)
			.build();
	}

	private CredentialRepresentation createPasswordCredential(String password) {
		CredentialRepresentation credential = new CredentialRepresentation();
		credential.setType(CredentialRepresentation.PASSWORD);
		credential.setValue(password);
		credential.setTemporary(false);
		return credential;
	}

	private String extractUserId(Response response) {
		String locationHeader = response.getHeaderString("Location");
		if (locationHeader != null) {
			return locationHeader.substring(locationHeader.lastIndexOf("/") + 1);
		}
		return null;
	}

	private void assignRealmRole(RealmResource realmResource, String userId, String roleName) {
		log.info("Assigning realm role {} to user {}", roleName, userId);

		try {
			// Get the role representation
			var rolesResource = realmResource.roles();
			RoleRepresentation role = rolesResource.get(roleName)
				.toRepresentation();

			if (role == null) {
				log.warn("Role {} not found, skipping assignment", roleName);
				return;
			}

			// Assign the role to the user
			var userResource = realmResource.users()
				.get(userId);
			userResource.roles()
				.realmLevel()
				.add(Collections.singletonList(role));

			log.info("Successfully assigned role {} to user {}", roleName, userId);

		} catch (Exception e) {
			log.error("Error assigning realm role: {}", e.getMessage(), e);
			// Don't throw - user creation should succeed even if role assignment fails
		}
	}

	public List<UserRepresentation> searchUsers(String username) {
		Keycloak keycloak = getKeycloakClient();
		RealmResource realmResource = keycloak.realm(keycloakProperties.getRealm());
		UsersResource usersResource = realmResource.users();
		return usersResource.search(username, true);
	}

	public void deleteUser(String userId) {
		Keycloak keycloak = getKeycloakClient();
		RealmResource realmResource = keycloak.realm(keycloakProperties.getRealm());
		UsersResource usersResource = realmResource.users();
		usersResource.delete(userId);
	}

	@Override
	public String createGroup(String groupName) {
		log.info("Creating group in Keycloak: {}", groupName);

		Keycloak keycloak = getKeycloakClient();
		RealmResource realmResource = keycloak.realm(keycloakProperties.getRealm());
		GroupsResource groupsResource = realmResource.groups();

		// Create group representation
		GroupRepresentation group = new GroupRepresentation();
		group.setName(groupName);

		// Create the group
		Response response = groupsResource.add(group);

		if (response.getStatus() == 201) {
			String locationHeader = response.getHeaderString("Location");
			String groupId = locationHeader.substring(locationHeader.lastIndexOf("/") + 1);
			log.info("Group created successfully with ID: {}", groupId);
			return groupId;
		} else if (response.getStatus() == 409) {
			log.error("Group already exists: {}", groupName);
			throw new RuntimeException("Group already exists: " + groupName);
		} else {
			String errorMessage = "Failed to create group. Status: " + response.getStatus();
			log.error(errorMessage);
			throw new RuntimeException(errorMessage);
		}
	}

	@Override
	public void updateUserAttributes(String userId, Map<String, List<String>> attributes) {
		log.info("Updating attributes for user: {}", userId);

		Keycloak keycloak = getKeycloakClient();
		RealmResource realmResource = keycloak.realm(keycloakProperties.getRealm());
		UsersResource usersResource = realmResource.users();

		// Get the user resource
		UserResource userResource = usersResource.get(userId);

		// Get current user representation
		UserRepresentation userRepresentation = userResource.toRepresentation();

		if (userRepresentation == null) {
			throw new RuntimeException("User not found with ID: " + userId);
		}

		// Get existing attributes
		Map<String, List<String>> existingAttributes = userRepresentation.getAttributes();
		if (existingAttributes == null) {
			existingAttributes = new HashMap<>();
		}

		// Merge attributes - new values override existing ones
		for (Map.Entry<String, List<String>> entry : attributes.entrySet()) {
			String key = entry.getKey();
			List<String> value = entry.getValue();
			existingAttributes.put(key, value);
			log.debug("Merging attribute: {} = {}", key, value);
		}

		// Update user with merged attributes
		userRepresentation.setAttributes(existingAttributes);
		userResource.update(userRepresentation);

		log.info("User attributes updated successfully for user: {}", userId);
	}
}
