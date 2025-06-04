package com.blm.takeout.entity;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "recommendations")
public class Recommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Integer id;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private Integer itemId;

    @Column(nullable = false, precision = 10, scale = 2)
    private Double score;
} 