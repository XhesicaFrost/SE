package com.blm.takeout.dto;

import com.blm.takeout.entity.Order;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private Integer userId;
    private Integer shopId;
    private String shopName;
    private String shopImage;
    private BigDecimal totalAmount;
    private Order.OrderStatus status;
    private String deliveryAddress;
    private String deliveryPhone;
    private String deliveryName;
    private Integer riderId;
    private String riderName;
    private String riderPhone;
    private String riderLocation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String orderNumber;
    private List<OrderItemDTO> orderItems;
} 