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

@Service
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final ItemService itemService;
    private final ShopRepository shopRepository;

    public CartService(CartItemRepository cartItemRepository, 
                      ItemService itemService,
                      ShopRepository shopRepository) {
        this.cartItemRepository = cartItemRepository;
        this.itemService = itemService;
        this.shopRepository = shopRepository;
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

    public List<CartDTO> getCartItems(Integer userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        
        // 按店铺分组
        Map<Integer, List<CartItem>> shopItemsMap = cartItems.stream()
            .collect(Collectors.groupingBy(item -> {
                Item product = itemService.getItemById(item.getItemId());
                return product.getShopId();
            }));
            
        return shopItemsMap.entrySet().stream()
            .map(entry -> {
                CartDTO cartDTO = new CartDTO();
                
                // 设置店铺信息
                Shop shop = shopRepository.findById(entry.getKey())
                    .orElseThrow(() -> new RuntimeException("店铺不存在"));
                Map<String, Object> shopInfo = new HashMap<>();
                shopInfo.put("id", shop.getId().toString());
                shopInfo.put("name", shop.getName());
                shopInfo.put("image", shop.getImage());
                shopInfo.put("address", shop.getAddress());
                cartDTO.setShop(shopInfo);
                
                // 设置商品信息
                List<CartItemDTO> items = entry.getValue().stream()
                    .map(cartItem -> {
                        CartItemDTO itemDTO = new CartItemDTO();
                        Item product = itemService.getItemById(cartItem.getItemId());
                        
                        Map<String, Object> productInfo = new HashMap<>();
                        productInfo.put("id", product.getId().toString());
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

    public CartItem updateCartItem(Integer userId, Integer itemId, Integer change) {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }

    public List<CartItem> getCartItems(Integer userId) {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void deleteCartItem(Integer userId, Integer itemId) {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void clearCart(Integer userId) {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }
} 