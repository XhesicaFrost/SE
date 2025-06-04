package com.blm.takeout.service;

import com.blm.takeout.entity.Order;
import com.blm.takeout.entity.OrderItem;
import com.blm.takeout.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderHistoryService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderHistoryService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Map<String, Object>> getUserOrderHistory(Integer userId, Integer limit) {
        List<Order> orders = orderRepository.findByUser_useridOrderByCreatedAtDesc(userId);
        
        if (limit != null && limit > 0) {
            orders = orders.stream()
                .limit(limit)
                .collect(Collectors.toList());
        }

        return orders.stream()
            .map(order -> {
                Map<String, Object> orderData = Map.of(
                    "orderId", order.getId(),
                    "shopId", order.getShop().getId(),
                    "items", order.getOrderItems().stream()
                        .map(item -> Map.of(
                            "itemId", item.getItemId(),
                            "quantity", item.getQuantity(),
                            "price", item.getPrice()
                        ))
                        .collect(Collectors.toList()),
                    "totalAmount", order.getTotalAmount(),
                    "createTime", order.getCreatedAt()
                );
                return orderData;
            })
            .collect(Collectors.toList());
    }
} 