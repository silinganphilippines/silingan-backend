package com.ria.olita.tech.silingan.service;

import com.ria.olita.tech.silingan.dto.req.CreateAnnouncementRequest;
import com.ria.olita.tech.silingan.dto.req.UpdateAnnouncementRequest;
import com.ria.olita.tech.silingan.dto.res.AnnouncementResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AnnouncementService {

    AnnouncementResponse createAnnouncement(CreateAnnouncementRequest request);

    AnnouncementResponse updateAnnouncement(UUID id, UpdateAnnouncementRequest request);

    Page<AnnouncementResponse> getCommunityAnnouncements(UUID communityId, Pageable pageable);

    Page<AnnouncementResponse> getActiveCommunityAnnouncements(UUID communityId, Pageable pageable);

    AnnouncementResponse getAnnouncementById(UUID id);

    void deleteAnnouncement(UUID id);
    
    AnnouncementResponse publishAnnouncement(UUID id);
    
    AnnouncementResponse unpublishAnnouncement(UUID id);
}
