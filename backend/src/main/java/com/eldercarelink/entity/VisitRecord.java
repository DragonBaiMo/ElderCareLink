package com.eldercarelink.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 志愿者探访记录实体。
 */
@Entity
@Table(name = "t_visit_record")
@Data
public class VisitRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long elderId;

    private Long volunteerId;
    private LocalDateTime visitTime;
    private String visitType;
    private String serviceContent;
    private String elderStatus;
    private String nextAdvice;
    private LocalDateTime createTime = LocalDateTime.now();
}
