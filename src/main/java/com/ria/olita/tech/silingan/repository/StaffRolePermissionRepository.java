package com.ria.olita.tech.silingan.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ria.olita.tech.silingan.entity.StaffRolePermission;
import com.ria.olita.tech.silingan.entity.rbac.PermissionEnum;
import com.ria.olita.tech.silingan.entity.rbac.StaffRoleCode;

@Repository
public interface StaffRolePermissionRepository extends JpaRepository<StaffRolePermission, UUID> {

	@Query("SELECT srp.permission FROM StaffRolePermission srp WHERE srp.staffRole.id = :staffRoleId")
	List<PermissionEnum> findPermissionsByStaffRoleId(UUID staffRoleId);

	@Query("SELECT srp.permission FROM StaffRolePermission srp WHERE srp.staffRole.code = :roleCode")
	List<PermissionEnum> findPermissionsByRoleCode(StaffRoleCode roleCode);

	List<StaffRolePermission> findByStaffRoleId(UUID staffRoleId);
}
