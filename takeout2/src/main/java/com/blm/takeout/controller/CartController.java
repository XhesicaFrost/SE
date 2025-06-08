package com.blm.takeout.controller;

import com.blm.takeout.entity.CartItem;
import com.blm.takeout.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<CartItem> addToCart(
            @RequestParam Integer userId,
            @RequestParam Integer itemId,
            @RequestParam Integer quantity) {
        CartItem cartItem = cartService.addToCart(userId, itemId, quantity);
        return ResponseEntity.ok(cartItem);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItem>> getCartItems(@PathVariable Integer userId) {
        List<CartItem> cartItems = cartService.getCartItems(userId);
        return ResponseEntity.ok(cartItems);
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateQuantity(
            @RequestParam Integer userId,
            @RequestParam Integer itemId,
            @RequestParam Integer quantity) {
        cartService.updateQuantity(userId, itemId, quantity);
        return ResponseEntity.ok().build();
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