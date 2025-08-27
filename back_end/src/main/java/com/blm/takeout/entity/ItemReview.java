package com.blm.takeout.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "item_reviews")
public class ItemReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "item_id", nullable = false)
    private Integer itemId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private Integer rating;

    @Column
    private String content;

    @Column
    private String images;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
} 