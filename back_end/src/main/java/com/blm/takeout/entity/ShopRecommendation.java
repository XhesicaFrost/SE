package com.blm.takeout.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "shop_recommendations")
public class ShopRecommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Column(nullable = false)
    private Double score;

    @Column(name = "recommendation_reason")
    private String recommendationReason;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
} 