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
import com.blm.takeout.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
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
            
        List<CartDTO> result = new ArrayList<>();
        
        for (Map.Entry<Integer, List<CartItem>> entry : shopItemsMap.entrySet()) {
            CartDTO cartDTO = new CartDTO();
            
            // 设置店铺信息
            Shop shop = shopRepository.findById(entry.getKey())
                .orElseThrow(() -> new RuntimeException("店铺不存在"));
            Map<String, Object> shopInfo = new HashMap<>();
            shopInfo.put("id", shop.getId());
            shopInfo.put("name", shop.getName());
            
            // 将店铺图片转换为base64
            if (shop.getImage() != null && !shop.getImage().isEmpty()) {
                try {
                    String base64Image = FileUtils.convertImageToBase64(shop.getImage());
                    shopInfo.put("image", base64Image);
                } catch (IOException e) {
                    e.printStackTrace();
                    shopInfo.put("image", "");
                }
            } else {
                shopInfo.put("image", "");
            }
            
            shopInfo.put("address", shop.getAddress());
            cartDTO.setShop(shopInfo);
            
            // 设置商品信息
            List<CartItemDTO> items = new ArrayList<>();
            for (CartItem cartItem : entry.getValue()) {
                CartItemDTO itemDTO = new CartItemDTO();
                Item item = cartItem.getItem();
                Map<String, Object> productInfo = new HashMap<>();
                productInfo.put("id", item.getId());
                productInfo.put("name", item.getName());
                productInfo.put("description", item.getDescription());
                productInfo.put("price", item.getPrice());
                
                // 将商品图片转换为base64
                if (item.getImage() != null && !item.getImage().isEmpty()) {
                    try {
                        String base64Image = FileUtils.convertImageToBase64(item.getImage());
                        productInfo.put("image", base64Image);
                    } catch (IOException e) {
                        e.printStackTrace();
                        productInfo.put("image", "");
                    }
                } else {
                    productInfo.put("image", "");
                }
                
                itemDTO.setProduct(productInfo);
                itemDTO.setQuantity(cartItem.getQuantity());
                items.add(itemDTO);
            }
            cartDTO.setItems(items);
            result.add(cartDTO);
        }
        
        return result;
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