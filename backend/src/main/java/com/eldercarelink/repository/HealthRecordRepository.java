package com.eldercarelink.repository;

import com.eldercarelink.entity.HealthRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

/**
 * 健康记录仓储。
 */
public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {
    Page<HealthRecord> findByElderIdAndRecordDateBetween(Long elderId, LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<HealthRecord> findByElderId(Long elderId, Pageable pageable);

    java.util.List<HealthRecord> findTop5ByElderIdOrderByRecordDateDesc(Long elderId);
}
