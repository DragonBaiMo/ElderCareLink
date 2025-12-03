package com.eldercarelink.repository;

import com.eldercarelink.entity.VolunteerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 志愿者资料仓储。
 */
public interface VolunteerProfileRepository extends JpaRepository<VolunteerProfile, Long> {
    Optional<VolunteerProfile> findByUserId(Long userId);

    List<VolunteerProfile> findByUserIdIn(List<Long> userIds);
}
