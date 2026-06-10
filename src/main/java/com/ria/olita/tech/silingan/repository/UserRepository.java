package com.ria.olita.tech.silingan.repository;

import com.ria.olita.tech.silingan.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
	Optional<User> findByKeycloakUserId(String keycloakUserId);


	@Query("""
	           SELECT u.id
	           FROM User u
	           WHERE u.keycloakUserId = ?1
	       """)
	Optional<String> getUserIdByKeycloakUserId(String keycloakUserId);
}
