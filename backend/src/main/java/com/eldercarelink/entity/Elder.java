package com.eldercarelink.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 老人基本信息实体。
 */
@Entity
@Table(name = "t_elder")
@Data
public class Elder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    private String gender;
    private Integer age;
    private String idCard;
    private String phone;
    private String address;
    private String livingType;
    private String healthLevel;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelation;
    private String remarks;
    /**
     * 负责该老人的志愿者 ID。
     */
    private Long responsibleVolunteerId;
    /**
     * 负责志愿者姓名，便于前端快速展示。
     */
    private String responsibleVolunteerName;
    private String status = "ACTIVE";
    private LocalDateTime createTime = LocalDateTime.now();
}
