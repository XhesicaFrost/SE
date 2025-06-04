package com.blm.takeout.service;

import com.blm.takeout.entity.Item;
import com.blm.takeout.entity.ItemCategory;
import com.blm.takeout.entity.Shop;
import com.blm.takeout.repository.ItemRepository;
import com.blm.takeout.repository.ItemCategoryRepository;
import com.blm.takeout.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShopDetailService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemCategoryRepository itemCategoryRepository;

    @Autowired
    private ShopRepository shopRepository;

    public Map<String, Object> getShopDetail(Integer shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("店铺不存在"));

        Map<String, Object> result = new HashMap<>();
        result.put("id", shop.getId());
        result.put("name", shop.getName());
        result.put("type", shop.getType());
        result.put("rating", shop.getRating());
        result.put("minPrice", shop.getMinPrice());
        result.put("maxPrice", shop.getMinPrice() + 100.0); // 这里可以根据实际情况计算
        result.put("deliveryTime", shop.getDeliveryTime());
        result.put("location", Map.of("latitude", shop.getLatitude(), "longitude", shop.getLongitude()));
        result.put("address", shop.getAddress());
        result.put("businessHours", shop.getBusinessHours());
        result.put("phone", shop.getPhone());
        result.put("description", shop.getDescription());
        
        // 获取热销商品
        List<Item> hotItems = itemRepository.findHotItemsByShopId(shopId);
        result.put("hotItems", hotItems.stream()
            .map(this::convertItemToMap)
            .collect(Collectors.toList()));
        
        // 获取商品分类及商品
        List<ItemCategory> categories = itemCategoryRepository.findByShopIdOrderBySortOrderAsc(shopId);
        List<Map<String, Object>> categoryList = new ArrayList<>();
        
        for (ItemCategory category : categories) {
            Map<String, Object> categoryMap = new HashMap<>();
            categoryMap.put("id", category.getId());
            categoryMap.put("name", category.getName());
            
            List<Item> items = itemRepository.findByShopIdAndCategoryId(shopId, category.getId());
            categoryMap.put("items", items.stream()
                .map(this::convertItemToMap)
                .collect(Collectors.toList()));
            
            categoryList.add(categoryMap);
        }
        
        result.put("categories", categoryList);
        
        return result;
    }

    public List<Map<String, Object>> getHotItems(Integer shopId, Integer limit) {
        List<Item> hotItems = itemRepository.findHotItemsByShopId(shopId);
        if (limit != null && limit > 0) {
            hotItems = hotItems.stream()
                .limit(limit)
                .collect(Collectors.toList());
        }
        
        return hotItems.stream()
            .map(this::convertItemToMap)
            .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getCategories(Integer shopId) {
        List<ItemCategory> categories = itemCategoryRepository.findByShopIdOrderBySortOrderAsc(shopId);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (ItemCategory category : categories) {
            Map<String, Object> categoryMap = new HashMap<>();
            categoryMap.put("id", category.getId());
            categoryMap.put("name", category.getName());
            
            List<Item> items = itemRepository.findByShopIdAndCategoryId(shopId, category.getId());
            categoryMap.put("items", items.stream()
                .map(this::convertItemToMap)
                .collect(Collectors.toList()));
            
            result.add(categoryMap);
        }
        
        return result;
    }

    private Map<String, Object> convertItemToMap(Item item) {
        Map<String, Object> itemMap = new HashMap<>();
        itemMap.put("id", item.getId());
        itemMap.put("name", item.getName());
        itemMap.put("price", item.getPrice());
        itemMap.put("image", item.getImage());
        itemMap.put("description", item.getDescription());
        itemMap.put("sales", item.getSales());
        itemMap.put("rating", item.getRating());
        return itemMap;
    }
} 