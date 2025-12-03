package com.eldercarelink.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告实体。
 */
@Entity
@Table(name = "t_announcement")
@Data
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    private String content;

    private Long publisherId;
    private LocalDateTime publishTime;
    private String status;
    private String type;
    private LocalDateTime createTime = LocalDateTime.now();
}
