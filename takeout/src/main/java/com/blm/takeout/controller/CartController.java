package com.blm.takeout.controller;

import com.blm.takeout.dto.CartDTO;
import com.blm.takeout.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Map<String, Object>> getCartItems(@RequestParam Integer userId) {
        try {
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
    public ResponseEntity<Map<String, Object>> addToCart(
            @RequestParam Integer userId,
            @RequestParam Integer itemId,
            @RequestParam Integer quantity) {
        try {
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
    public ResponseEntity<Map<String, Object>> updateQuantity(
            @RequestParam Integer userId,
            @RequestParam Integer itemId,
            @RequestParam Integer quantity) {
        try {
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

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeFromCart(
            @RequestParam Integer userId,
            @RequestParam Integer itemId) {
        cartService.removeFromCart(userId, itemId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Integer userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok().build();
    }
} 