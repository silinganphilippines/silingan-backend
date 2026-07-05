package com.ria.olita.tech.silingan.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ria.olita.tech.silingan.entity.UserCommunityPermission;
import com.ria.olita.tech.silingan.entity.rbac.PermissionEnum;

@Repository
public interface UserCommunityPermissionRepository extends JpaRepository<UserCommunityPermission, UUID> {

	List<UserCommunityPermission> findByUserIdAndCommunityId(UUID userId, UUID communityId);

	@Query("SELECT ucp.permission from UserCommunityPermission ucp WHERE ucp.user.id = :userId AND ucp.community.id = :communityId")
	List<PermissionEnum> findPermissionsByUserIdAndCommunityId(UUID userId, UUID communityId);

	@Query("SELECT DISTINCT ucp.user.id FROM UserCommunityPermission ucp WHERE ucp.community.id = :communityId")
	List<UUID> findUserIdsWithPermissionsInCommunity(UUID communityId);

	@Modifying
	@Query("DELETE FROM UserCommunityPermission ucp WHERE ucp.user.id = :userId AND ucp.community.id = :communityId")
	void deleteAllByUserIdAndCommunityId(UUID userId, UUID communityId);

	boolean existsByUserIdAndCommunityIdAndPermission(UUID userId, UUID communityId, PermissionEnum permission);
}
