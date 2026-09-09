package com.ria.olita.tech.silingan.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ria.olita.tech.silingan.entity.CommunityRolePermissionOverride;
import com.ria.olita.tech.silingan.entity.rbac.StaffRoleCode;

@Repository
public interface CommunityRolePermissionOverrideRepository extends JpaRepository<CommunityRolePermissionOverride, UUID> {

	List<CommunityRolePermissionOverride> findByCommunityIdAndStaffRoleId(UUID communityId, UUID staffRoleId);

	void deleteByCommunityIdAndStaffRoleId(UUID communityId, UUID staffRoleId);

	@Query("""
		SELECT cro
		FROM CommunityRolePermissionOverride cro
		WHERE cro.community.id = :communityId
			AND cro.staffRole.code = :roleCode
		""")
	List<CommunityRolePermissionOverride> findByCommunityIdAndRoleCode(UUID communityId, StaffRoleCode roleCode);
}
