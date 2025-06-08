package com.blm.takeout.service.impl;

import com.blm.takeout.entity.CartItem;
import com.blm.takeout.entity.Item;
import com.blm.takeout.repository.CartItemRepository;
import com.blm.takeout.repository.ItemRepository;
import com.blm.takeout.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public CartServiceImpl(CartItemRepository cartItemRepository, ItemRepository itemRepository) {
        this.cartItemRepository = cartItemRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public CartItem updateCartItem(Integer userId, Integer itemId, Integer change) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        CartItem cartItem = cartItemRepository.findByUserIdAndItemId(userId, itemId)
                .orElseGet(() -> {
                    CartItem newCartItem = new CartItem();
                    newCartItem.setUserId(userId);
                    newCartItem.setItemId(itemId);
                    newCartItem.setQuantity(0);
                    newCartItem.setPrice(item.getPrice());
                    newCartItem.setSelected(true);
                    return newCartItem;
                });

        int newQuantity = cartItem.getQuantity() + change;
        if (newQuantity <= 0) {
            cartItemRepository.deleteByUserIdAndItemId(userId, itemId);
            return null;
        }

        cartItem.setQuantity(newQuantity);
        return cartItemRepository.save(cartItem);
    }

    @Override
    public List<CartItem> getCartItems(Integer userId) {
        return cartItemRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteCartItem(Integer userId, Integer itemId) {
        cartItemRepository.deleteByUserIdAndItemId(userId, itemId);
    }

    @Override
    @Transactional
    public void clearCart(Integer userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        cartItems.forEach(item -> cartItemRepository.delete(item));
    }
} 