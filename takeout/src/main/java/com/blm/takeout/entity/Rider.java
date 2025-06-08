package com.blm.takeout.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "riders")
public class Rider {
    @Id
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false)
    private String password;

    @Column(name = "id_card", nullable = false)
    private String idCard;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiderStatus status;

    @Column(name = "current_latitude")
    private Double currentLatitude;

    @Column(name = "current_longitude")
    private Double currentLongitude;

    @Column(name = "current_location")
    private String currentLocation;

    @Column(name = "is_smart_dispatch")
    private Boolean isSmartDispatch = true;

    @Column(name = "service_rating")
    private Double serviceRating = 5.0;

    @Column(name = "total_orders")
    private Integer totalOrders = 0;

    @Column(name = "completed_orders")
    private Integer completedOrders = 0;

    @Column(name = "active_orders")
    private Integer activeOrders = 0;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum RiderStatus {
        ONLINE,         // 在线，可接单
        OFFLINE,        // 离线，不接单
        DELIVERING,     // 配送中
        RESTING        // 休息中
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = RiderStatus.OFFLINE;
        this.isSmartDispatch = true;
        this.serviceRating = 5.0;
        this.totalOrders = 0;
        this.completedOrders = 0;
        this.activeOrders = 0;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Integer getActiveOrderCount() {
        return this.activeOrders;
    }

    public void incrementActiveOrders() {
        this.activeOrders = this.activeOrders + 1;
        this.totalOrders = this.totalOrders + 1;
        this.status = RiderStatus.DELIVERING;
    }

    public void decrementActiveOrders() {
        if (this.activeOrders > 0) {
            this.activeOrders--;
            if (this.activeOrders == 0 && this.status == RiderStatus.DELIVERING) {
                this.status = RiderStatus.ONLINE;
            }
        }
    }

    public void updateStatusBasedOnActiveOrders() {
        if (this.activeOrders > 0) {
            this.status = RiderStatus.DELIVERING;
        } else if (this.status == RiderStatus.DELIVERING) {
            this.status = RiderStatus.ONLINE;
        }
    }
} 