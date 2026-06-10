package com.ria.olita.tech.silingan.service;

import com.ria.olita.tech.silingan.dto.req.CreateIssueCategoryRequest;
import com.ria.olita.tech.silingan.dto.res.IssueCategoryResponse;

import java.util.List;
import java.util.UUID;

public interface IssueCategoryService {

	IssueCategoryResponse create(CreateIssueCategoryRequest request);

	IssueCategoryResponse getById(UUID id);

	List<IssueCategoryResponse> getByCommunityId(UUID communityId);

	List<IssueCategoryResponse> getActiveByCommunityId(UUID communityId);

	List<IssueCategoryResponse> getAll();

	List<IssueCategoryResponse> getAllIncludingDeleted();

//	IssueCategoryResponse update(UUID id, UpdateIssueCategoryRequest request);

	void delete(UUID id);

	IssueCategoryResponse restore(UUID id);

	IssueCategoryResponse toggleActive(UUID id);
}
