package com.blm.takeout.controller;

import com.blm.takeout.entity.Item;
import com.blm.takeout.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.stream.Collectors;
import com.blm.takeout.dto.ItemDTO;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/items")
    public ResponseEntity<List<ItemDTO>> getItemsByShopId(@RequestParam Integer shopId) {
        try {
            List<ItemDTO> items = itemService.getItemsByShopId(shopId);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/api/items/search")
    public ResponseEntity<Page<Item>> searchItems(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Item> items = itemService.searchItems(keyword, pageRequest);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/api/items/shop/{shopId}")
    public ResponseEntity<Map<String, Object>> getItemsByShopId(
            @PathVariable Integer shopId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Item> items = itemService.getItemsByShopId(shopId, pageRequest);
        
        List<Map<String, Object>> itemList = items.getContent().stream()
            .map(item -> {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("id", item.getId());
                itemMap.put("image", item.getImage());
                itemMap.put("name", item.getName());
                itemMap.put("description", item.getDescription());
                itemMap.put("price", item.getPrice());
                return itemMap;
            })
            .collect(Collectors.toList());
            
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("success", true);
        response.put("data", itemList);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/items/{itemId}")
    public Map<String, Object> getItemDetails(@PathVariable Integer itemId) {
        try {
            return Map.of(
                "code", 200,
                "success", true,
                "data", itemService.getItemDetails(itemId)
            );
        } catch (Exception e) {
            return Map.of(
                "code", 500,
                "success", false,
                "message", "获取商品详情失败：" + e.getMessage()
            );
        }
    }
} 