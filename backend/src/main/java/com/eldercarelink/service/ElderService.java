package com.eldercarelink.service;

import com.eldercarelink.dto.ElderDetailResponse;
import com.eldercarelink.entity.Elder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

/**
 * 老人信息业务接口。
 */
public interface ElderService {
    Elder create(Elder elder);

    Elder update(Long id, Elder elder);

    void disable(Long id);

    Page<Elder> list(String name, String livingType, String healthLevel, String status, Long volunteerId, Pageable pageable);

    ElderDetailResponse detail(Long id);

    ResponseEntity<byte[]> exportAll();
}
