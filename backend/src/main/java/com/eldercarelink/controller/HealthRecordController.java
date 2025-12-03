package com.eldercarelink.controller;

import com.eldercarelink.common.ApiResponse;
import com.eldercarelink.common.BusinessException;
import com.eldercarelink.entity.HealthRecord;
import com.eldercarelink.entity.User;
import com.eldercarelink.repository.ElderRepository;
import com.eldercarelink.repository.HealthRecordRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 健康记录接口，支持新增与查询。
 */
@RestController
@RequestMapping("/api/health-records")
@RequiredArgsConstructor
public class HealthRecordController {
    private final HealthRecordRepository healthRecordRepository;
    private final ElderRepository elderRepository;

    @PostMapping
    public ApiResponse<HealthRecord> create(@AuthenticationPrincipal User currentUser, @Valid @RequestBody HealthRecord record) {
        elderRepository.findById(record.getElderId()).orElseThrow(() -> new BusinessException("老人不存在"));
        record.setCreatedBy(currentUser.getId());
        if (record.getRecordDate() == null) {
            record.setRecordDate(LocalDateTime.now());
        }
        return ApiResponse.success(healthRecordRepository.save(record));
    }

    @GetMapping
    public ApiResponse<Page<HealthRecord>> list(@RequestParam Long elderId,
                                                @RequestParam(required = false) LocalDateTime start,
                                                @RequestParam(required = false) LocalDateTime end,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<HealthRecord> result;
        if (start != null && end != null) {
            result = healthRecordRepository.findByElderIdAndRecordDateBetween(elderId, start, end, pageable);
        } else {
            result = healthRecordRepository.findByElderId(elderId, pageable);
        }
        return ApiResponse.success(result);
    }

    @PutMapping("/{id}")
    public ApiResponse<HealthRecord> update(@PathVariable Long id, @AuthenticationPrincipal User user, @Valid @RequestBody HealthRecord record) {
        HealthRecord exist = healthRecordRepository.findById(id).orElseThrow(() -> new BusinessException("记录不存在"));
        elderRepository.findById(record.getElderId()).orElseThrow(() -> new BusinessException("老人不存在"));
        record.setId(exist.getId());
        record.setCreatedBy(user.getId());
        if (record.getRecordDate() == null) {
            record.setRecordDate(exist.getRecordDate());
        }
        return ApiResponse.success(healthRecordRepository.save(record));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        healthRecordRepository.deleteById(id);
        return ApiResponse.success("已删除", null);
    }
}
