package com.blm.takeout.service.impl;

import com.blm.takeout.entity.CartItem;
import com.blm.takeout.entity.Item;
import com.blm.takeout.entity.Shop;
import com.blm.takeout.repository.CartItemRepository;
import com.blm.takeout.repository.ItemRepository;
import com.blm.takeout.repository.ShopRepository;
import com.blm.takeout.service.CartService;
import com.blm.takeout.dto.CartDTO;
import com.blm.takeout.dto.CartItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;
    private final ShopRepository shopRepository;

    @Autowired
    public CartServiceImpl(CartItemRepository cartItemRepository, 
                          ItemRepository itemRepository,
                          ShopRepository shopRepository) {
        this.cartItemRepository = cartItemRepository;
        this.itemRepository = itemRepository;
        this.shopRepository = shopRepository;
    }

    @Override
    @Transactional
    public CartItem updateCartItem(Integer userId, Integer itemId, Integer change) {
        Optional<CartItem> existingItem = cartItemRepository.findByUserIdAndItemId(userId, itemId);
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("商品不存在"));

        CartItem cartItem;
        if (existingItem.isPresent()) {
            cartItem = existingItem.get();
            int newQuantity = cartItem.getQuantity() + change;
            if (newQuantity <= 0) {
                cartItemRepository.delete(cartItem);
                return null;
            }
            cartItem.setQuantity(newQuantity);
        } else {
            if (change <= 0) {
                throw new RuntimeException("商品数量不能为负数");
            }
            cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setItem(item);
            cartItem.setQuantity(change);
        }

        return cartItemRepository.save(cartItem);
    }

    @Override
    public List<CartDTO> getCartItems(Integer userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        
        // 按店铺分组
        Map<Integer, List<CartItem>> shopItemsMap = cartItems.stream()
            .collect(Collectors.groupingBy(item -> item.getItem().getShopId()));
            
        return shopItemsMap.entrySet().stream()
            .map(entry -> {
                CartDTO cartDTO = new CartDTO();
                
                // 设置店铺信息
                Shop shop = shopRepository.findById(entry.getKey())
                    .orElseThrow(() -> new RuntimeException("店铺不存在"));
                Map<String, Object> shopInfo = new HashMap<>();
                shopInfo.put("id", shop.getId());
                shopInfo.put("name", shop.getName());
                shopInfo.put("image", shop.getImage());
                shopInfo.put("address", shop.getAddress());
                cartDTO.setShop(shopInfo);
                
                // 设置商品信息
                List<CartItemDTO> items = entry.getValue().stream()
                    .map(cartItem -> {
                        CartItemDTO itemDTO = new CartItemDTO();
                        Item product = cartItem.getItem();
                        
                        Map<String, Object> productInfo = new HashMap<>();
                        productInfo.put("id", product.getId());
                        productInfo.put("name", product.getName());
                        productInfo.put("description", product.getDescription());
                        productInfo.put("price", product.getPrice());
                        productInfo.put("image", product.getImage());
                        
                        itemDTO.setProduct(productInfo);
                        itemDTO.setQuantity(cartItem.getQuantity());
                        return itemDTO;
                    })
                    .collect(Collectors.toList());
                    
                cartDTO.setItems(items);
                return cartDTO;
            })
            .collect(Collectors.toList());
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