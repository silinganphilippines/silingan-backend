package com.ria.olita.tech.silingan.service;

import com.ria.olita.tech.silingan.dto.req.CreateUserRequest;

import java.util.List;
import java.util.Map;

public interface KeycloakService {

	String createUser(CreateUserRequest request);

	void createGroup(String groupName);

	void updateUserAttributes(String keycloakUserId, Map<String, List<String>> attributes);

	Map<String, List<String>> getUserAttributes(String keycloakUserId);

	List<String> getRealmRoles(String keycloakUserId);

}
