package com.blm.takeout.dto;

import com.blm.takeout.entity.Order.OrderStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Integer id;
    private String orderNumber;
    private Integer userId;
    private Integer shopId;
    private String shopName;
    private String shopImage;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String deliveryAddress;
    private Double deliveryLatitude;
    private Double deliveryLongitude;
    private String deliveryPhone;
    private String deliveryName;
    private Integer riderId;
    private String riderName;
    private String riderPhone;
    private Double riderLatitude;
    private Double riderLongitude;
    private String riderLocation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime estimatedDeliveryTime;
    private LocalDateTime pickupTime;
    private LocalDateTime completedTime;
    private LocalDateTime riderArrivedShopTime;
    private Boolean isSmartAssignment;
    private Integer urgentCount;
    private Boolean isUrgent;
    private String plannedRoute;
    private List<OrderItemDTO> orderItems;
} 