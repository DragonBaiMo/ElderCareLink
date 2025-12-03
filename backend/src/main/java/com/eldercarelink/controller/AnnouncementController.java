package com.eldercarelink.controller;

import com.eldercarelink.common.ApiResponse;
import com.eldercarelink.common.BusinessException;
import com.eldercarelink.entity.Announcement;
import com.eldercarelink.entity.User;
import com.eldercarelink.repository.AnnouncementRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 公告管理接口。
 */
@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {
    private final AnnouncementRepository announcementRepository;

    @PostMapping
    public ApiResponse<Announcement> create(@AuthenticationPrincipal User user, @Valid @RequestBody Announcement announcement) {
        announcement.setPublisherId(user.getId());
        announcement.setPublishTime(LocalDateTime.now());
        if (announcement.getStatus() == null) {
            announcement.setStatus("PUBLISHED");
        }
        return ApiResponse.success(announcementRepository.save(announcement));
    }

    @PutMapping("/{id}")
    public ApiResponse<Announcement> update(@PathVariable Long id, @AuthenticationPrincipal User user, @Valid @RequestBody Announcement announcement) {
        Announcement exist = announcementRepository.findById(id).orElseThrow(() -> new BusinessException("公告不存在"));
        announcement.setId(exist.getId());
        announcement.setPublisherId(user.getId());
        announcement.setPublishTime(exist.getPublishTime());
        return ApiResponse.success(announcementRepository.save(announcement));
    }

    @GetMapping
    public ApiResponse<Page<Announcement>> list(@RequestParam(defaultValue = "") String status,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (status != null && !status.isEmpty()) {
            return ApiResponse.success(announcementRepository.findByStatus(status, pageable));
        }
        return ApiResponse.success(announcementRepository.findAll(pageable));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        Announcement announcement = announcementRepository.findById(id).orElseThrow();
        announcement.setStatus(status);
        announcementRepository.save(announcement);
        return ApiResponse.success("状态已更新", null);
    }

    @GetMapping("/{id}")
    public ApiResponse<Announcement> detail(@PathVariable Long id) {
        return ApiResponse.success(announcementRepository.findById(id).orElseThrow());
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        announcementRepository.deleteById(id);
        return ApiResponse.success("已删除", null);
    }
}
