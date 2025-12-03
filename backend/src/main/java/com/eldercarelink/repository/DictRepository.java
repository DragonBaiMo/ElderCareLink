package com.eldercarelink.repository;

import com.eldercarelink.entity.DictEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 字典仓储。
 */
public interface DictRepository extends JpaRepository<DictEntry, Long> {
    List<DictEntry> findByDictType(String dictType);
}
