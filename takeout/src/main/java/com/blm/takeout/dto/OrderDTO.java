package com.blm.takeout.dto;

import com.blm.takeout.entity.Order;
import lombok.Data;
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
    private Double totalAmount;
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
    private List<OrderItemDTO> orderItems;
} 