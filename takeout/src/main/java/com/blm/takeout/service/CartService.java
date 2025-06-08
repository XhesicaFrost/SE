package com.blm.takeout.service;

import com.blm.takeout.dto.CartDTO;
import com.blm.takeout.dto.CartItemDTO;
import com.blm.takeout.entity.CartItem;
import com.blm.takeout.entity.Item;
import com.blm.takeout.entity.Shop;
import com.blm.takeout.repository.CartItemRepository;
import com.blm.takeout.repository.ShopRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

public interface CartService {
    CartItem updateCartItem(Integer userId, Integer itemId, Integer change);
    List<CartDTO> getCartItems(Integer userId);
    void deleteCartItem(Integer userId, Integer itemId);
    void clearCart(Integer userId);
} 