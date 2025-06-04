package com.blm.takeout.service;

import com.blm.takeout.entity.CartItem;
import com.blm.takeout.entity.Item;
import com.blm.takeout.repository.CartItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final ItemService itemService;

    public CartService(CartItemRepository cartItemRepository, ItemService itemService) {
        this.cartItemRepository = cartItemRepository;
        this.itemService = itemService;
    }

    @Transactional
    public CartItem addToCart(Integer userId, Integer itemId, Integer quantity) {
        Item item = itemService.getItemById(itemId);
        CartItem existingItem = cartItemRepository.findByUserIdAndItemId(userId, itemId);
        
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setPrice(item.getPrice());
            return cartItemRepository.save(existingItem);
        }

        CartItem cartItem = new CartItem();
        cartItem.setUserId(userId);
        cartItem.setItemId(itemId);
        cartItem.setQuantity(quantity);
        cartItem.setPrice(item.getPrice());
        return cartItemRepository.save(cartItem);
    }

    public List<CartItem> getCartItems(Integer userId) {
        return cartItemRepository.findByUserId(userId);
    }

    @Transactional
    public void updateQuantity(Integer userId, Integer itemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findByUserIdAndItemId(userId, itemId);
        if (cartItem != null) {
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }
    }

    @Transactional
    public void removeFromCart(Integer userId, Integer itemId) {
        cartItemRepository.deleteByUserIdAndItemId(userId, itemId);
    }

    @Transactional
    public void clearCart(Integer userId) {
        cartItemRepository.deleteByUserId(userId);
    }
} 