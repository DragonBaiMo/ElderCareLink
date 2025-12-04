package com.eldercarelink.repository;

import com.eldercarelink.entity.Elder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 老人信息仓储。
 */
public interface ElderRepository extends JpaRepository<Elder, Long> {
    Page<Elder> findByNameContainingAndLivingTypeContainingAndHealthLevelContainingAndStatusContaining(String name, String livingType, String healthLevel, String status, Pageable pageable);

    Page<Elder> findByResponsibleVolunteerIdAndStatusContaining(Long responsibleVolunteerId, String status, Pageable pageable);
}
