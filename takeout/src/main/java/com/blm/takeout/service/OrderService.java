package com.blm.takeout.service;

import com.blm.takeout.dto.OrderDTO;
import com.blm.takeout.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Page<OrderDTO> getUserOrders(Integer userId, Pageable pageable);
    OrderDTO getOrderDetail(String orderNumber);
    void updateOrderStatus(Integer orderId, Order.OrderStatus status);
    void updateRiderLocation(Integer orderId, String location);
} 