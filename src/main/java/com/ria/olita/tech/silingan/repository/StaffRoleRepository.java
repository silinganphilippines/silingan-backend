package com.ria.olita.tech.silingan.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ria.olita.tech.silingan.entity.StaffRole;
import com.ria.olita.tech.silingan.entity.rbac.StaffRoleCode;

@Repository
public interface StaffRoleRepository extends JpaRepository<StaffRole, UUID> {

	Optional<StaffRole> findByCode(StaffRoleCode code);

	List<StaffRole> findAllByOrderByNameAsc();
}
