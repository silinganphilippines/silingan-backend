package com.ria.olita.tech.silingan.service.impl;

import com.ria.olita.tech.silingan.dto.req.CreateCommunityRequest;
import com.ria.olita.tech.silingan.dto.req.UpdateCommunityRequest;
import com.ria.olita.tech.silingan.dto.res.CommunityCodeResponse;
import com.ria.olita.tech.silingan.dto.res.CommunityResponse;
import com.ria.olita.tech.silingan.entity.Address;
import com.ria.olita.tech.silingan.entity.Community;
import com.ria.olita.tech.silingan.entity.CommunityStatus;
import com.ria.olita.tech.silingan.entity.CommunityType;
import com.ria.olita.tech.silingan.exception.BadRequestException;
import com.ria.olita.tech.silingan.exception.ResourceNotFoundException;
import com.ria.olita.tech.silingan.mapper.AddressMapper;
import com.ria.olita.tech.silingan.mapper.CommunityMapper;
import com.ria.olita.tech.silingan.repository.CommunityRepository;
import com.ria.olita.tech.silingan.repository.TenantRepository;
import com.ria.olita.tech.silingan.repository.UserRepository;
import com.ria.olita.tech.silingan.security.context.UserContextHolder;
import com.ria.olita.tech.silingan.service.CommunityCodeService;
import com.ria.olita.tech.silingan.service.CommunityService;
import com.ria.olita.tech.silingan.service.KeycloakService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

	private final CommunityRepository communityRepository;
	private final TenantRepository tenantRepository;
	private final UserRepository userRepository;
	private final CommunityMapper communityMapper;
	private final AddressMapper addressMapper;
	private final KeycloakService keycloakService;
	private final CommunityCodeService communityCodeService;

	@Override
	public boolean validate(String communityId) {
		try {
			UUID id = UUID.fromString(communityId);
			return communityRepository.existsById(id);
		} catch (IllegalArgumentException e) {
			return communityRepository.findByCode(communityId)
				.isPresent();
		}
	}

	@Override
	public CommunityResponse create(CreateCommunityRequest request) {
		CommunityCodeResponse codeResponse = communityCodeService.generateCodes(request.type(), request.address());
		Community community = communityMapper.toEntity(request);
		community.setStatus(CommunityStatus.ACTIVE);
		community.setCommunityCode(codeResponse.displayCode());
		community.setSystemGenCode(codeResponse.systemCode());

		// Handle address
		if (request.address() != null) {
			Address address = addressMapper.toEntity(request.address());
			community.setAddress(address);
		}

		var tenant = tenantRepository.findById(request.tenantId())
			.orElseThrow(() -> new ResourceNotFoundException("Tenant", "id", request.tenantId()));
		community.setTenant(tenant);

		Community createdCommunity = communityRepository.save(community);
		// Create group in keycloak
		System.out.println("Generated systemcode: "+ createdCommunity.getSystemGenCode());
		System.out.println("Generated systemcode: "+ createdCommunity.getCommunityCode());

		keycloakService.createGroup(createdCommunity.getCommunityCode());
		return communityMapper.toResponse(createdCommunity);
	}

	@Override
	@Transactional(readOnly = true)
	public CommunityResponse getById(UUID id) {
		Community community = communityRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Community", "id", id));
		return communityMapper.toResponse(community);
	}

	@Override
	@Transactional(readOnly = true)
	public CommunityResponse getByCode(String code) {
		Community community = communityRepository.findByCode(code)
			.orElseThrow(() -> new ResourceNotFoundException("Community", "code", code));
		return communityMapper.toResponse(community);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CommunityResponse> getAll() {
		return communityRepository.findAll()
			.stream()
			.map(communityMapper::toResponse)
			.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<CommunityResponse> getByStatus(CommunityStatus status) {
		return communityRepository.findByStatus(status)
			.stream()
			.map(communityMapper::toResponse)
			.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<CommunityResponse> getByType(CommunityType type) {
		return communityRepository.findByType(type)
			.stream()
			.map(communityMapper::toResponse)
			.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<CommunityResponse> getByUserId(UUID userId) {
		var user = userRepository.findById(userId)
			.orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
		return user.getJoinedCommunities()
			.stream()
			.map(communityMapper::toResponse)
			.collect(Collectors.toList());
	}

	@Override
	public CommunityResponse update(UUID id, UpdateCommunityRequest request) {
		Community community = communityRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Community", "id", id));

		communityMapper.updateEntityFromRequest(request, community);

		// Handle address update
		if (request.address() != null) {
			if (community.getAddress() != null) {
				addressMapper.updateEntityFromRequest(request.address(), community.getAddress());
			} else {
				Address address = addressMapper.toEntity(request.address());
				community.setAddress(address);
			}
		}

		if (community.getTenant() == null) {
			throw new BadRequestException("Community tenant is required");
		}

		Community updated = communityRepository.save(community);
		return communityMapper.toResponse(updated);
	}

	@Override
	public void delete(UUID id) {
		if (!communityRepository.existsById(id)) {
			throw new ResourceNotFoundException("Community", "id", id);
		}
		communityRepository.deleteById(id);
	}


	@Override
	public void switchCommunity(UUID communityId) {
		keycloakService.updateUserAttributes(
			UserContextHolder.get()
				.keycloakUserId(),
			Map.of("communityId", List.of(communityId.toString()))
		);
	}

	@Override
	public CommunityResponse archive(UUID id) {
		Community community = communityRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Community", "id", id));

		if (community.getStatus() == CommunityStatus.ARCHIVE) {
			throw new BadRequestException("Community is already archived");
		}

		community.setStatus(CommunityStatus.ARCHIVE);
		Community archived = communityRepository.save(community);
		return communityMapper.toResponse(archived);
	}
}
