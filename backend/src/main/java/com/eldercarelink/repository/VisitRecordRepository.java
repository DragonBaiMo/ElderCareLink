package com.eldercarelink.repository;

import com.eldercarelink.entity.VisitRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

/**
 * 探访记录仓储。
 */
public interface VisitRecordRepository extends JpaRepository<VisitRecord, Long> {
    Page<VisitRecord> findByVolunteerId(Long volunteerId, Pageable pageable);
    Page<VisitRecord> findByElderId(Long elderId, Pageable pageable);
    Page<VisitRecord> findByVolunteerIdAndElderId(Long volunteerId, Long elderId, Pageable pageable);
    Page<VisitRecord> findByVisitTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    java.util.List<VisitRecord> findTop5ByElderIdOrderByVisitTimeDesc(Long elderId);
}
