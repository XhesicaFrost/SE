package com.blm.takeout.controller;

import com.blm.takeout.common.ApiResponse;
import com.blm.takeout.entity.Order;
import com.blm.takeout.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminOrderController {
    private final OrderService orderService;

    @GetMapping("/orders")
    public ApiResponse<?> getAllOrders() {
        try {
            List<Order> orders = orderService.getAllOrders();
            List<Map<String, Object>> orderList = orders.stream()
                .map(order -> {
                    Map<String, Object> orderMap = new HashMap<>();
                    orderMap.put("id", order.getId());
                    orderMap.put("status", order.getStatus().toString().toLowerCase());
                    orderMap.put("shopName", order.getShopName());
                    orderMap.put("createTime", order.getCreatedAt());
                    orderMap.put("totalAmount", order.getTotalAmount());
                    orderMap.put("items", order.getOrderItems().stream()
                        .map(item -> {
                            Map<String, Object> itemMap = new HashMap<>();
                            itemMap.put("name", item.getItemName());
                            itemMap.put("quantity", item.getQuantity());
                            return itemMap;
                        })
                        .collect(Collectors.toList()));
                    return orderMap;
                })
                .collect(Collectors.toList());
            return ApiResponse.success(orderList);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }
} 