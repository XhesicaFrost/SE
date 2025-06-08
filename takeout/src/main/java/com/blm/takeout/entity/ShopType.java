package com.blm.takeout.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "shop_types")
public class ShopType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 100)
    private String icon;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
} 