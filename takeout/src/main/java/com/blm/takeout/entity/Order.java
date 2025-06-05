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
    private Long id;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private Integer shopId;

    @Column(nullable = false)
    private String shopName;

    @Column(nullable = false)
    private String shopImage;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private String deliveryAddress;

    @Column(nullable = false)
    private String deliveryPhone;

    @Column(nullable = false)
    private String deliveryName;

    private Integer riderId;
    private String riderName;
    private String riderPhone;
    private String riderLocation;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false, unique = true)
    private String orderNumber;

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