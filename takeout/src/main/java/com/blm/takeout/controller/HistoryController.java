package com.blm.takeout.controller;

import com.blm.takeout.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class HistoryController {

    private final OrderService orderService;

    @Autowired
    public HistoryController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getOrderHistory(@RequestParam Integer userId) {
        try {
            List<Map<String, Object>> orders = orderService.getOrderHistory(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("data", orders);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "获取历史订单失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
} 