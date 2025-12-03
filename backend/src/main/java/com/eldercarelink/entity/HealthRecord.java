package com.eldercarelink.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 健康记录实体，用于记录日常指标。
 */
@Entity
@Table(name = "t_health_record")
@Data
public class HealthRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long elderId;

    private LocalDateTime recordDate;
    private String bloodPressure;
    private Integer heartRate;
    private Double temperature;
    private Double bloodSugar;
    private String healthDesc;
    private Long createdBy;
    private LocalDateTime createTime = LocalDateTime.now();
}
