package com.blm.takeout.controller;

import com.blm.takeout.dto.OrderDTO;
import com.blm.takeout.entity.Order.OrderStatus;
import com.blm.takeout.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getUserOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 401);
                response.put("success", false);
                response.put("message", "用户未登录");
                return ResponseEntity.ok(response);
            }
            
            Integer userId = Integer.parseInt(authentication.getName());
            Page<OrderDTO> orders = orderService.getUserOrders(userId, PageRequest.of(page, size));
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("data", orders.getContent());
            response.put("total", orders.getTotalElements());
            response.put("totalPages", orders.getTotalPages());
            response.put("currentPage", orders.getNumber());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "获取订单失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> getOrderDetail(@PathVariable Integer orderId) {
        try {
            OrderDTO order = orderService.getOrderDetail(orderId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("data", order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @PathVariable Integer orderId,
            @RequestParam OrderStatus status) {
        try {
            orderService.updateOrderStatus(orderId, status);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("message", "订单状态更新成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @PutMapping("/{orderId}/rider-location")
    public ResponseEntity<Map<String, Object>> updateRiderLocation(
            @PathVariable Integer orderId,
            @RequestParam String location) {
        try {
            orderService.updateRiderLocation(orderId, location);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("message", "骑手位置更新成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
} 