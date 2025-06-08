package com.blm.takeout.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tag")
@Data
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String tag;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;
} 