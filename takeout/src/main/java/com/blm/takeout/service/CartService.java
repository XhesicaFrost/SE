package com.blm.takeout.service;

import com.blm.takeout.dto.CartDTO;
import com.blm.takeout.entity.CartItem;

import java.util.*;

public interface CartService {
    CartItem updateCartItem(Integer userId, Integer itemId, Integer change);
    List<CartDTO> getCartItems(Integer userId);
    void deleteCartItem(Integer userId, Integer itemId);
    void clearCart(Integer userId);
} 