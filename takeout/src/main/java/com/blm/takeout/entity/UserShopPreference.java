package com.blm.takeout.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "user_shop_preferences")
public class UserShopPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Integer id;

    @Column(nullable = false)
    private Integer userId;

    @Column(columnDefinition = "json")
    private List<String> preferredTypes;

    @Column(precision = 10, scale = 2)
    private Double minPrice;

    @Column(precision = 10, scale = 2)
    private Double maxPrice;

    private Integer maxDeliveryTime;

    @Column(nullable = false)
    private java.time.LocalDateTime createdAt;

    @Column(nullable = false)
    private java.time.LocalDateTime updatedAt;
} 