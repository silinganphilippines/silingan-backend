package com.ria.olita.tech.silingan.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ria.olita.tech.silingan.entity.Address;
import com.ria.olita.tech.silingan.entity.CommunityRole;
import com.ria.olita.tech.silingan.entity.UserCommunity;

@Repository
public interface UserCommunityRepository extends JpaRepository<UserCommunity, UUID> {

	@Query("SELECT CASE WHEN COUNT(uc) > 0 THEN true ELSE false END " +
			"FROM UserCommunity uc " +
			"WHERE uc.community.id = :communityId AND uc.role = :role")
	boolean hasRoleInCommunity(UUID communityId, CommunityRole role);
}
