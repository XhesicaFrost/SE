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

    @Column(name = "rider_location")
    private String riderLocation;

    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    public enum OrderStatus {
        PENDING_PAYMENT,    // 待支付
        PAID,              // 已支付
        PREPARING,         // 商家备餐中
        READY,             // 商家已出餐
        DELIVERING,        // 配送中
        COMPLETED,         // 已完成
        CANCELLED          // 已取消
    }
} 