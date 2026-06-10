package com.ria.olita.tech.silingan.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ria.olita.tech.silingan.entity.Tenant;
import com.ria.olita.tech.silingan.entity.TenantStatus;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {
	List<Tenant> findByStatus(TenantStatus status);
}
