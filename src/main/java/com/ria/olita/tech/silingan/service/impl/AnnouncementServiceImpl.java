package com.ria.olita.tech.silingan.service.impl;

import com.ria.olita.tech.silingan.dto.req.CreateAnnouncementRequest;
import com.ria.olita.tech.silingan.dto.req.UpdateAnnouncementRequest;
import com.ria.olita.tech.silingan.dto.res.AnnouncementResponse;
import com.ria.olita.tech.silingan.entity.Announcement;
import com.ria.olita.tech.silingan.entity.AnnouncementStatus;
import com.ria.olita.tech.silingan.entity.Community;
import com.ria.olita.tech.silingan.exception.NotFoundException;
import com.ria.olita.tech.silingan.mapper.AnnouncementMapper;
import com.ria.olita.tech.silingan.repository.AnnouncementRepository;
import com.ria.olita.tech.silingan.repository.CommunityRepository;
import com.ria.olita.tech.silingan.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final CommunityRepository communityRepository;
    private final AnnouncementMapper announcementMapper;

    @Override
    @Transactional
    public AnnouncementResponse createAnnouncement(CreateAnnouncementRequest request) {
        Community community = communityRepository.findById(request.communityId())
                .orElseThrow(() -> new NotFoundException("Community not found with id: " + request.communityId()));

        Announcement announcement = announcementMapper.toEntity(request);
        announcement.setCommunity(community);

        Announcement saved = announcementRepository.save(announcement);
        return announcementMapper.toResponse(saved);
    }


    @Override
    @Transactional
    public AnnouncementResponse updateAnnouncement(UUID id, UpdateAnnouncementRequest request) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Announcement not found with id: " + id));

        announcementMapper.updateEntityFromRequest(request, announcement);
        Announcement saved = announcementRepository.save(announcement);
        return announcementMapper.toResponse(saved);
    }

    @Override
    public Page<AnnouncementResponse> getCommunityAnnouncements(UUID communityId, Pageable pageable) {
        return announcementRepository.findByCommunityId(communityId, pageable)
                .map(announcementMapper::toResponse);
    }

    @Override
    public Page<AnnouncementResponse> getActiveCommunityAnnouncements(UUID communityId, Pageable pageable) {
        return announcementRepository.findActiveByCommunityId(communityId, AnnouncementStatus.PUBLISHED, pageable)
                .map(announcementMapper::toResponse);
    }

    @Override
    public AnnouncementResponse getAnnouncementById(UUID id) {
        return announcementRepository.findById(id)
                .map(announcementMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Announcement not found with id: " + id));
    }

    @Override
    @Transactional
    public void deleteAnnouncement(UUID id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Announcement not found with id: " + id));
        announcement.setDeleted(true);
        announcementRepository.save(announcement);
    }

    @Override
    @Transactional
    public AnnouncementResponse publishAnnouncement(UUID id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Announcement not found with id: " + id));
        announcement.setStatus(AnnouncementStatus.PUBLISHED);
        Announcement saved = announcementRepository.save(announcement);
        return announcementMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public AnnouncementResponse unpublishAnnouncement(UUID id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Announcement not found with id: " + id));
        announcement.setStatus(AnnouncementStatus.DRAFT);
        Announcement saved = announcementRepository.save(announcement);
        return announcementMapper.toResponse(saved);
    }
}
