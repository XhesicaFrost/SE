package com.blm.takeout.controller;

import com.blm.takeout.dto.CartDTO;
import com.blm.takeout.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/shopcart")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getCartItems(HttpServletRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 401);
                response.put("success", false);
                response.put("message", "用户未登录");
                return ResponseEntity.status(401).body(response);
            }
            
            // 从请求头中获取token
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 401);
                response.put("success", false);
                response.put("message", "无效的认证信息");
                return ResponseEntity.status(401).body(response);
            }
            
            String token = authHeader.substring(7); // 移除"Bearer "前缀
            String[] parts = token.split("\\.");
            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> claims = mapper.readValue(payload, Map.class);
            
            // 打印claims内容以便调试
            System.out.println("JWT Claims: " + claims);
            
            // 尝试不同的字段名
            Object userIdObj = claims.get("userId");
            if (userIdObj == null) {
                userIdObj = claims.get("userid");
            }
            if (userIdObj == null) {
                userIdObj = claims.get("user_id");
            }
            
            if (userIdObj == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 401);
                response.put("success", false);
                response.put("message", "无效的用户信息");
                return ResponseEntity.status(401).body(response);
            }
            
            Integer userId;
            if (userIdObj instanceof Integer) {
                userId = (Integer) userIdObj;
            } else if (userIdObj instanceof String) {
                userId = Integer.parseInt((String) userIdObj);
            } else {
                userId = Integer.parseInt(userIdObj.toString());
            }
            
            List<CartDTO> cartItems = cartService.getCartItems(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("data", cartItems);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "获取购物车信息失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/shopcart/add")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> addToCart(
            @RequestParam Integer itemId,
            @RequestParam Integer quantity) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 401);
                response.put("success", false);
                response.put("message", "用户未登录");
                return ResponseEntity.status(401).body(response);
            }
            
            Integer userId = Integer.parseInt(authentication.getName());
            cartService.addToCart(userId, itemId, quantity);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("message", "添加成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "添加失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/shopcart/update")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> updateQuantity(
            @RequestParam Integer itemId,
            @RequestParam Integer quantity) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 401);
                response.put("success", false);
                response.put("message", "用户未登录");
                return ResponseEntity.status(401).body(response);
            }
            
            Integer userId = Integer.parseInt(authentication.getName());
            cartService.updateQuantity(userId, itemId, quantity);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("message", "更新成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "更新失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @DeleteMapping("/shopcart/remove")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> removeFromCart(@RequestParam Integer itemId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 401);
                response.put("success", false);
                response.put("message", "用户未登录");
                return ResponseEntity.status(401).body(response);
            }
            
            Integer userId = Integer.parseInt(authentication.getName());
            cartService.removeFromCart(userId, itemId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("message", "删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "删除失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @DeleteMapping("/shopcart/clear")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> clearCart() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 401);
                response.put("success", false);
                response.put("message", "用户未登录");
                return ResponseEntity.status(401).body(response);
            }
            
            Integer userId = Integer.parseInt(authentication.getName());
            cartService.clearCart(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("message", "清空成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "清空失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
} 