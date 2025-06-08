package com.blm.takeout.controller;

import com.blm.takeout.entity.CartItem;
import com.blm.takeout.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PutMapping("/{userId}/{itemId}")
    public ResponseEntity<CartItem> updateCartItem(
            @PathVariable Integer userId,
            @PathVariable Integer itemId,
            @RequestParam Integer change) {
        CartItem cartItem = cartService.updateCartItem(userId, itemId, change);
        return ResponseEntity.ok(cartItem);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItem>> getCartItems(@PathVariable Integer userId) {
        List<CartItem> cartItems = cartService.getCartItems(userId);
        return ResponseEntity.ok(cartItems);
    }

    @DeleteMapping("/{userId}/{itemId}")
    public ResponseEntity<Void> deleteCartItem(
            @PathVariable Integer userId,
            @PathVariable Integer itemId) {
        cartService.deleteCartItem(userId, itemId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Integer userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok().build();
    }
} 