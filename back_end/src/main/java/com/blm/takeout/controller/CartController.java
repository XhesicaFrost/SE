package com.blm.takeout.controller;

import com.blm.takeout.entity.CartItem;
import com.blm.takeout.service.CartService;
import com.blm.takeout.dto.CartDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PutMapping("/cart_items/{userId}/{itemId}")
    public ResponseEntity<CartItem> updateCartItem(
            @PathVariable Integer userId,
            @PathVariable Integer itemId,
            @RequestParam Integer change) {
        CartItem cartItem = cartService.updateCartItem(userId, itemId, change);
        return ResponseEntity.ok(cartItem);
    }

    @GetMapping("/cart_items/{userId}")
    public ResponseEntity<List<CartDTO>> getCartItems(@PathVariable Integer userId) {
        List<CartDTO> cartItems = cartService.getCartItems(userId);
        return ResponseEntity.ok(cartItems);
    }

    @GetMapping("/cart_items/user/shopcart")
    public ResponseEntity<Map<String, Object>> getUserCartItems(@RequestParam Integer userId) {
        try {
            List<CartDTO> cartItems = cartService.getCartItems(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("data", cartItems);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/edit/shopcart")
    public ResponseEntity<Map<String, Object>> editCartItem(
            @RequestParam Integer userId,
            @RequestParam Integer productId,
            @RequestParam(required = false) Integer change) {
        try {
            cartService.updateCartItem(userId, productId, change != null ? change : 1);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/cart_items/{userId}/{itemId}")
    public ResponseEntity<Void> deleteCartItem(
            @PathVariable Integer userId,
            @PathVariable Integer itemId) {
        cartService.deleteCartItem(userId, itemId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/cart_items/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Integer userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok().build();
    }
} 