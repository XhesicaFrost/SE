package com.blm.takeout.service;

import com.blm.takeout.entity.Order;
import com.blm.takeout.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
        PageRequest pageRequest = PageRequest.of(0, limit != null ? limit : Integer.MAX_VALUE);
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageRequest).getContent();
        
        return orders.stream()
            .map(order -> {
                Map<String, Object> orderData = Map.of(
                    "orderId", order.getId(),
                    "shopId", order.getShopId(),
                    "items", order.getOrderItems().stream()
                        .map(item -> Map.of(
                            "itemId", item.getItemId(),
                            "quantity", item.getQuantity(),
                            "unitPrice", item.getUnitPrice(),
                            "totalPrice", item.getUnitPrice() * item.getQuantity()
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