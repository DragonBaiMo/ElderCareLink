package com.eldercarelink.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 通用字典表，维护枚举配置。
 */
@Entity
@Table(name = "t_dict")
@Data
public class DictEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String dictType;

    @Column(nullable = false)
    private String dictKey;

    @Column(nullable = false)
    private String dictValue;

    private Integer sort;
    private String status = "ENABLED";
}
