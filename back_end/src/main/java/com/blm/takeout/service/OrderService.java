package com.blm.takeout.service;

import com.blm.takeout.dto.OrderDTO;
import com.blm.takeout.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface OrderService {
    Page<OrderDTO> getUserOrders(Integer userId, Pageable pageable);
    OrderDTO getOrderDetail(Integer orderId);
    OrderDTO getOrderDetail(String orderNumber);
    Page<Order> getOrdersByUserId(Integer userId, PageRequest pageRequest);
    Order getOrderById(Integer orderId);
    @Transactional
    Order createOrder(Order order);
    List<Map<String, Object>> getOrderHistory(Integer userId);
    List<Order> getAllOrders();
    List<Order> getOrdersByUserId(Integer userId);
    List<Order> getOrdersByShopId(Integer shopId);
    @Transactional
    void updateOrderStatus(Integer orderId, Order.OrderStatus status);
    @Transactional
    void updateRiderLocation(Integer orderId, String location);
} 