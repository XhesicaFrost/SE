package com.blm.takeout.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(unique = true)
    private String orderNumber;
    
    private Integer userId;
    private Integer shopId;
    private String shopName;
    private String shopImage;
    private String shopAddress;
    private Double totalAmount;
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    private String deliveryAddress;
    private String deliveryPhone;
    private String deliveryName;
    private Integer riderId;
    private String riderName;
    private String riderPhone;
    private String riderLocation;
    
    private LocalDateTime createdAt;
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