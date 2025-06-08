package com.blm.takeout.entity;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "item_categories")
public class ItemCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Integer id;

    @Column(nullable = false)
    private Integer shopId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private Integer sortOrder = 0;

    @Column(nullable = false)
    private java.time.LocalDateTime createdAt;
} 