package com.blm.takeout.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "shop")
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column
    private String image;

    @Column
    private Double rating;

    @Column(name = "avg_price")
    private Double avgPrice;

    @Column
    private Double distance;

    @Column(name = "deliver_time")
    private Integer deliverTime;

    @Column
    private String address;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Item> items;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Tag> tags;

    @Column
    private String description;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Integer sales;

    @Column(nullable = false)
    private Double minPrice;

    @Column(nullable = false)
    private Double maxPrice;

    @Column(nullable = false)
    private Double deliveryFee;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String businessHours;

    @Column(nullable = false)
    private Boolean isOpen;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
} 