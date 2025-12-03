package com.eldercarelink.controller;

import com.eldercarelink.common.ApiResponse;
import com.eldercarelink.common.BusinessException;
import com.eldercarelink.entity.User;
import com.eldercarelink.entity.VisitRecord;
import com.eldercarelink.repository.VisitRecordRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 探访记录接口。
 */
@RestController
@RequestMapping("/api/visit-records")
@RequiredArgsConstructor
public class VisitRecordController {
    private final VisitRecordRepository visitRecordRepository;

    @PostMapping
    public ApiResponse<VisitRecord> create(@AuthenticationPrincipal User user, @Valid @RequestBody VisitRecord record) {
        if (record.getVisitTime() == null) {
            record.setVisitTime(LocalDateTime.now());
        }
        record.setVolunteerId(user.getId());
        return ApiResponse.success(visitRecordRepository.save(record));
    }

    @GetMapping
    public ApiResponse<Page<VisitRecord>> list(@RequestParam(required = false) Long volunteerId,
                                               @RequestParam(required = false) Long elderId,
                                               @RequestParam(required = false) LocalDateTime start,
                                               @RequestParam(required = false) LocalDateTime end,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (start != null && end != null) {
            return ApiResponse.success(visitRecordRepository.findByVisitTimeBetween(start, end, pageable));
        }
        if (volunteerId != null && elderId != null) {
            return ApiResponse.success(visitRecordRepository.findByVolunteerIdAndElderId(volunteerId, elderId, pageable));
        }
        if (volunteerId != null) {
            return ApiResponse.success(visitRecordRepository.findByVolunteerId(volunteerId, pageable));
        }
        if (elderId != null) {
            return ApiResponse.success(visitRecordRepository.findByElderId(elderId, pageable));
        }
        return ApiResponse.success(visitRecordRepository.findAll(pageable));
    }

    @PutMapping("/{id}")
    public ApiResponse<VisitRecord> update(@PathVariable Long id, @Valid @RequestBody VisitRecord record) {
        VisitRecord exist = visitRecordRepository.findById(id).orElseThrow(() -> new BusinessException("记录不存在"));
        record.setId(exist.getId());
        if (record.getVisitTime() == null) {
            record.setVisitTime(exist.getVisitTime());
        }
        return ApiResponse.success(visitRecordRepository.save(record));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        visitRecordRepository.deleteById(id);
        return ApiResponse.success("已删除", null);
    }
}
