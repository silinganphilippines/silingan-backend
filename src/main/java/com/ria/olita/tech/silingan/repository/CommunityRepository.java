package com.ria.olita.tech.silingan.repository;

import com.ria.olita.tech.silingan.entity.Community;
import com.ria.olita.tech.silingan.entity.CommunityStatus;
import com.ria.olita.tech.silingan.entity.CommunityType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommunityRepository extends JpaRepository<Community, UUID> {

	@Query("Select c from Community c where c.communityCode = ?1")
	Optional<Community> findByCode(String code);

	@Query("select CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END FROM Community c WHERE c.communityCode = ?1")
	boolean existsByCode(String code);

	List<Community> findByStatus(CommunityStatus status);

	List<Community> findByType(CommunityType type);

	List<Community> findByStatusAndType(CommunityStatus status, CommunityType type);
}
