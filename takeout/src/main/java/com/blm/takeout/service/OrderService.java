package com.blm.takeout.service;

import com.blm.takeout.dto.OrderDTO;
import com.blm.takeout.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;

public interface OrderService {
    Page<OrderDTO> getUserOrders(Integer userId, Pageable pageable);
    OrderDTO getOrderDetail(Integer orderId);
    OrderDTO getOrderDetail(String orderNumber);
    Page<Order> getOrdersByUserId(Integer userId, PageRequest pageRequest);
    Order getOrderById(Integer orderId);
    Order createOrder(Order order);
    Order updateOrderStatus(Integer orderId, String status);
    Order updateRiderLocation(Integer orderId, String location);
    List<Map<String, Object>> getOrderHistory(Integer userId);
} 