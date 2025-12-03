package com.eldercarelink.repository;

import com.eldercarelink.entity.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 公告仓储。
 */
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    Page<Announcement> findByStatus(String status, Pageable pageable);
}
