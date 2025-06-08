package com.blm.takeout.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "order_number", unique = true)
    private String orderNumber;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;
    
    @Column(nullable = false)
    private BigDecimal totalAmount;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;
    
    @Column(name = "delivery_latitude")
    private Double deliveryLatitude;
    
    @Column(name = "delivery_longitude")
    private Double deliveryLongitude;
    
    @Column(name = "delivery_phone", nullable = false)
    private String deliveryPhone;
    
    @Column(name = "delivery_name", nullable = false)
    private String deliveryName;
    
    @Column(name = "rider_id")
    private Integer riderId;
    
    @Column(name = "rider_name")
    private String riderName;
    
    @Column(name = "rider_phone")
    private String riderPhone;
    
    @Column(name = "rider_latitude")
    private Double riderLatitude;
    
    @Column(name = "rider_longitude")
    private Double riderLongitude;
    
    @Column(name = "rider_location")
    private String riderLocation;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "estimated_delivery_time")
    private LocalDateTime estimatedDeliveryTime;
    
    @Column(name = "pickup_time")
    private LocalDateTime pickupTime;
    
    @Column(name = "completed_time")
    private LocalDateTime completedTime;
    
    @Column(name = "rider_arrived_shop_time")
    private LocalDateTime riderArrivedShopTime;
    
    @Column(name = "is_smart_assignment")
    private Boolean isSmartAssignment = false;
    
    @Column(name = "urgent_count")
    private Integer urgentCount = 0;
    
    @Column(name = "is_urgent")
    private Boolean isUrgent = false;
    
    @Column(name = "planned_route", columnDefinition = "TEXT")
    private String plannedRoute;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    public enum OrderStatus {
        PENDING_PAYMENT,    // 待支付
        PAID,              // 已支付
        PREPARING,         // 商家备餐中
        READY,             // 商家已出餐
        WAITING_RIDER,     // 等待骑手接单
        ACCEPTED,          // 骑手已接单
        PICKED,            // 骑手已取餐
        DELIVERING,        // 配送中
        COMPLETED,         // 已完成
        CANCELLED          // 已取消
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        urgentCount = 0;
        isUrgent = false;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 