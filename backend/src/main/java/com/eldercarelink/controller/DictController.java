package com.eldercarelink.controller;

import com.eldercarelink.common.ApiResponse;
import com.eldercarelink.entity.DictEntry;
import com.eldercarelink.repository.DictRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据字典接口。
 */
@RestController
@RequestMapping("/api/dicts")
@RequiredArgsConstructor
public class DictController {
    private final DictRepository dictRepository;

    @GetMapping("/{type}")
    public ApiResponse<List<DictEntry>> list(@PathVariable("type") String type) {
        return ApiResponse.success(dictRepository.findByDictType(type));
    }

    @PostMapping
    public ApiResponse<DictEntry> create(@Valid @RequestBody DictEntry entry) {
        return ApiResponse.success(dictRepository.save(entry));
    }

    @PutMapping("/{id}")
    public ApiResponse<DictEntry> update(@PathVariable Long id, @Valid @RequestBody DictEntry entry) {
        entry.setId(id);
        return ApiResponse.success(dictRepository.save(entry));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        dictRepository.deleteById(id);
        return ApiResponse.success("已删除", null);
    }
}
