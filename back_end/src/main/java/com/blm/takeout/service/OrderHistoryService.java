package com.blm.takeout.service;

import com.blm.takeout.entity.Order;
import com.blm.takeout.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.util.HashMap;

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
                Map<String, Object> orderData = new HashMap<>();
                orderData.put("orderId", order.getId());
                orderData.put("shopId", order.getShop().getId());
                orderData.put("items", order.getOrderItems().stream()
                    .map(item -> {
                        Map<String, Object> itemMap = new HashMap<>();
                        itemMap.put("itemId", item.getItem().getId());
                        itemMap.put("quantity", item.getQuantity());
                        itemMap.put("unitPrice", item.getUnitPrice());
                        itemMap.put("totalPrice", item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())));
                        return itemMap;
                    })
                    .collect(Collectors.toList()));
                orderData.put("totalAmount", order.getTotalAmount());
                orderData.put("createTime", order.getCreatedAt());
                return orderData;
            })
            .collect(Collectors.toList());
    }
} 