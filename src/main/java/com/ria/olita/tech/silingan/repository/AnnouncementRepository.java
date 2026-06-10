package com.ria.olita.tech.silingan.repository;

import com.ria.olita.tech.silingan.entity.Announcement;
import com.ria.olita.tech.silingan.entity.AnnouncementStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface AnnouncementRepository extends JpaRepository<Announcement, UUID> {

    @Query("SELECT a FROM Announcement a WHERE a.community.id = :communityId ORDER BY a.pinned DESC, a.createdAt DESC")
    Page<Announcement> findByCommunityId(@Param("communityId") UUID communityId, Pageable pageable);

    @Query("SELECT a FROM Announcement a WHERE a.community.id = :communityId AND a.status = :status " +
            "AND (a.publishAt IS NULL OR a.publishAt <= CURRENT_TIMESTAMP) " +
            "ORDER BY a.pinned DESC, a.createdAt DESC")
    Page<Announcement> findActiveByCommunityId(@Param("communityId") UUID communityId, 
                                               @Param("status") AnnouncementStatus status,
                                               Pageable pageable);
}
