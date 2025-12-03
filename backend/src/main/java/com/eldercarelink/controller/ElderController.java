package com.eldercarelink.controller;

import com.eldercarelink.common.ApiResponse;
import com.eldercarelink.dto.ElderDetailResponse;
import com.eldercarelink.entity.Elder;
import com.eldercarelink.service.ElderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 老人信息接口，支持增删改查和条件分页。
 */
@RestController
@RequestMapping("/api/elders")
@RequiredArgsConstructor
public class ElderController {
    private final ElderService elderService;

    @PostMapping
    public ApiResponse<Elder> create(@Valid @RequestBody Elder elder) {
        return ApiResponse.success(elderService.create(elder));
    }

    @GetMapping
    public ApiResponse<Page<Elder>> list(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(defaultValue = "") String name,
                                         @RequestParam(defaultValue = "") String livingType,
                                         @RequestParam(defaultValue = "") String healthLevel,
                                         @RequestParam(defaultValue = "") String status,
                                         @RequestParam(required = false) Long volunteerId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Elder> elders = elderService.list(name, livingType, healthLevel, status, volunteerId, pageable);
        return ApiResponse.success(elders);
    }

    @GetMapping("/{id}")
    public ApiResponse<ElderDetailResponse> detail(@PathVariable Long id) {
        return ApiResponse.success(elderService.detail(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<Elder> update(@PathVariable Long id, @Valid @RequestBody Elder elder) {
        return ApiResponse.success(elderService.update(id, elder));
    }

    @PatchMapping("/{id}/disable")
    public ApiResponse<Void> disable(@PathVariable Long id) {
        elderService.disable(id);
        return ApiResponse.success("已注销", null);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export() {
        return elderService.exportAll();
    }
}
