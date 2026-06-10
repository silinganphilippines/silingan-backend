package com.ria.olita.tech.silingan.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ria.olita.tech.silingan.entity.Issue;
import com.ria.olita.tech.silingan.entity.IssueStatus;

@Repository
public interface IssueRepository extends JpaRepository<Issue, UUID> {

	List<Issue> findByCommunityId(UUID communityId);

	List<Issue> findByCommunityIdAndStatus(UUID communityId, IssueStatus status);

	List<Issue> findByCommunityIdAndCategoryId(UUID communityId, UUID categoryId);

	List<Issue> findByReporterId(UUID reporterId);
}