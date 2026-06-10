package com.ria.olita.tech.silingan.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ria.olita.tech.silingan.entity.IssueCategory;

@Repository
public interface IssueCategoryRepository extends JpaRepository<IssueCategory, UUID> {

	List<IssueCategory> findByCommunityId(UUID communityId);

	List<IssueCategory> findByCommunityIdAndIsActiveTrue(UUID communityId);

	@Query("SELECT e FROM IssueCategory e WHERE e.id = :id")
	Optional<IssueCategory> findByIdIncludingDeleted(UUID id);
}