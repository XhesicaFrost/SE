package com.blm.takeout.service;

import com.blm.takeout.entity.Item;
import com.blm.takeout.entity.Shop;
import com.blm.takeout.repository.ItemRepository;
import com.blm.takeout.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShopDetailService {

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ItemRepository itemRepository;

    public Map<String, Object> getShopDetail(Integer shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("店铺不存在"));

        List<Item> items = itemRepository.findByShopId(shopId);

        Map<String, Object> result = new HashMap<>();
        result.put("id", shop.getId());
        result.put("name", shop.getName());
        result.put("image", shop.getImage());
        result.put("description", shop.getDescription());
        result.put("rating", shop.getRating());
        result.put("isOpen", shop.getIsOpen());
        result.put("items", convertItemsToMap(items));

        return result;
    }

    private List<Map<String, Object>> convertItemsToMap(List<Item> items) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Item item : items) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("id", item.getId());
            itemMap.put("name", item.getName());
            itemMap.put("description", item.getDescription());
            itemMap.put("price", item.getPrice());
            itemMap.put("image", item.getImage());
            itemMap.put("sales", item.getSales());
            itemMap.put("rating", item.getRating());
            itemMap.put("status", item.getStatus());
            result.add(itemMap);
        }
        return result;
    }
} 