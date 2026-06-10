package com.ria.olita.tech.silingan.service.impl;

import com.ria.olita.tech.silingan.dto.req.CreateIssueCategoryRequest;
import com.ria.olita.tech.silingan.dto.req.UpdateIssueCategoryRequest;
import com.ria.olita.tech.silingan.dto.res.IssueCategoryResponse;
import com.ria.olita.tech.silingan.entity.Community;
import com.ria.olita.tech.silingan.entity.IssueCategory;
import com.ria.olita.tech.silingan.exception.ResourceNotFoundException;
import com.ria.olita.tech.silingan.repository.CommunityRepository;
import com.ria.olita.tech.silingan.repository.IssueCategoryRepository;
import com.ria.olita.tech.silingan.service.IssueCategoryService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class IssueCategoryServiceImpl implements IssueCategoryService {

	private final IssueCategoryRepository issueCategoryRepository;
	private final CommunityRepository communityRepository;

	@Override
	public IssueCategoryResponse create(CreateIssueCategoryRequest request) {
		Community community = communityRepository.findById(request.communityId())
			.orElseThrow(() -> new ResourceNotFoundException("Community", "id", request.communityId()));

		IssueCategory category = IssueCategory.builder()
			.description(request.description())
			.community(community)
			.displayOrder(request.displayOrder())
			.isActive(true)
			.build();

		IssueCategory saved = issueCategoryRepository.save(category);
		return IssueCategoryResponse.fromEntity(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public IssueCategoryResponse getById(UUID id) {
		IssueCategory category = issueCategoryRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("IssueCategory", "id", id));
		return IssueCategoryResponse.fromEntity(category);
	}

	@Override
	@Transactional(readOnly = true)
	public List<IssueCategoryResponse> getByCommunityId(UUID communityId) {
		List<IssueCategory> categories = issueCategoryRepository.findByCommunityId(communityId);
		return categories.stream()
			.map(IssueCategoryResponse::fromEntity)
			.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<IssueCategoryResponse> getActiveByCommunityId(UUID communityId) {
		List<IssueCategory> categories = issueCategoryRepository.findByCommunityIdAndIsActiveTrue(communityId);
		return categories.stream()
			.map(IssueCategoryResponse::fromEntity)
			.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<IssueCategoryResponse> getAll() {
		// @Where(clause = "deleted = false") automatically filters deleted records
		return issueCategoryRepository.findAll()
			.stream()
			.map(IssueCategoryResponse::fromEntity)
			.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<IssueCategoryResponse> getAllIncludingDeleted() {
		// Use native query to bypass @Where filter
		return issueCategoryRepository.findAll()
			.stream()
			.map(IssueCategoryResponse::fromEntity)
			.collect(Collectors.toList());
	}

//	@Override
//	public IssueCategoryResponse update(UUID id, UpdateIssueCategoryRequest request) {
//		IssueCategory category = issueCategoryRepository.findById(id)
//			.orElseThrow(() -> new ResourceNotFoundException("IssueCategory", "id", id));
//
//		if (request.name() != null) {
//			category.setName(request.name());
//		}
//		if (request.description() != null) {
//			category.setDescription(request.description());
//		}
//		if (request.displayOrder() != null) {
//			category.setDisplayOrder(request.displayOrder());
//		}
//		if (request.isActive() != null) {
//			category.setIsActive(request.isActive());
//		}
//
//		IssueCategory updated = issueCategoryRepository.save(category);
//		return IssueCategoryResponse.fromEntity(updated);
//	}

	@Override
	public void delete(UUID id) {
		IssueCategory category = issueCategoryRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("IssueCategory", "id", id));
		category.setDeleted(true);
		issueCategoryRepository.save(category);
	}

	@Override
	public IssueCategoryResponse restore(UUID id) {
		IssueCategory category = issueCategoryRepository.findByIdIncludingDeleted(id)
			.orElseThrow(() -> new ResourceNotFoundException("IssueCategory", "id", id));
		category.setDeleted(false);
		IssueCategory restored = issueCategoryRepository.save(category);
		return IssueCategoryResponse.fromEntity(restored);
	}

	@Override
	public IssueCategoryResponse toggleActive(UUID id) {
		IssueCategory category = issueCategoryRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("IssueCategory", "id", id));

		category.setIsActive(!category.getIsActive());
		IssueCategory updated = issueCategoryRepository.save(category);
		return IssueCategoryResponse.fromEntity(updated);
	}
}
