package com.blm.takeout.controller;

import com.blm.takeout.service.OrderHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/orders")
public class OrderHistoryController {
    private final OrderHistoryService orderHistoryService;

    @Autowired
    public OrderHistoryController(OrderHistoryService orderHistoryService) {
        this.orderHistoryService = orderHistoryService;
    }

    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getUserOrderHistory(
            @RequestParam Integer userId,
            @RequestParam(required = false) Integer limit) {
        try {
            List<Map<String, Object>> orders = orderHistoryService.getUserOrderHistory(userId, limit);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("data", orders);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "获取订单历史失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
} 