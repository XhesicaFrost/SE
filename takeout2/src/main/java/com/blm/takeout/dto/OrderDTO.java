package com.blm.takeout.dto;

import com.blm.takeout.entity.Order.OrderStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Integer id;
    private Integer userId;
    private Integer shopId;
    private String shopName;
    private String shopImage;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String deliveryAddress;
    private String deliveryPhone;
    private String deliveryName;
    private Integer riderId;
    private String riderName;
    private String riderPhone;
    private String riderLocation;//For example:"116.4074,39.9042"
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemDTO> orderItems;
} 