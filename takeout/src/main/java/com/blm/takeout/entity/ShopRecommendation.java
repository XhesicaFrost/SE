package com.blm.takeout.entity;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "shop_recommendations")
public class ShopRecommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Integer id;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private Integer shopId;

    @Column(nullable = false, precision = 10, scale = 2)
    private Double score;

    @Column(length = 200)
    private String recommendationReason;

    @Column(nullable = false)
    private java.time.LocalDateTime createdAt;
} 