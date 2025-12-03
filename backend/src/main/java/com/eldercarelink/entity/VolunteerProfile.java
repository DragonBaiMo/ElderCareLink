package com.eldercarelink.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 志愿者扩展资料，记录所属机构与备注。
 */
@Entity
@Table(name = "t_volunteer_profile")
@Data
public class VolunteerProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private String organization;

    private String remark;
}
