package com.blm.takeout.service.impl;

import com.blm.takeout.dto.OrderDTO;
import com.blm.takeout.dto.OrderItemDTO;
import com.blm.takeout.entity.Order;
import com.blm.takeout.entity.OrderItem;
import com.blm.takeout.repository.OrderRepository;
import com.blm.takeout.repository.UserRepository;
import com.blm.takeout.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    public Page<OrderDTO> getUserOrders(Integer userId, Pageable pageable) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Page<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return orders.map(this::convertToDTO);
    }

    public OrderDTO getOrderDetail(Integer orderId) {
        Order order = orderRepository.findById(orderId.longValue())
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        return convertToDTO(order);
    }

    @Transactional
    public void updateOrderStatus(Integer orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId.longValue())
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        order.setStatus(status);
        orderRepository.save(order);
    }

    @Transactional
    public void updateRiderLocation(Integer orderId, String location) {
        Order order = orderRepository.findById(orderId.longValue())
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        order.setRiderLocation(location);
        orderRepository.save(order);
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setShopId(order.getShopId());
        dto.setShopName(order.getShopName());
        dto.setShopImage(order.getShopImage());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setDeliveryPhone(order.getDeliveryPhone());
        dto.setDeliveryName(order.getDeliveryName());
        dto.setRiderId(order.getRiderId());
        dto.setRiderName(order.getRiderName());
        dto.setRiderPhone(order.getRiderPhone());
        dto.setRiderLocation(order.getRiderLocation());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setOrderItems(order.getOrderItems().stream()
                .map(this::convertToItemDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private OrderItemDTO convertToItemDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        dto.setItemId(item.getItemId());
        dto.setItemName(item.getItemName());
        dto.setItemImage(item.getItemImage());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setTotalPrice(item.getTotalPrice());
        return dto;
    }

    @Override
    public OrderDTO getOrderDetail(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        return convertToDTO(order);
    }
} 