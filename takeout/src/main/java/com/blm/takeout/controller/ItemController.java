package com.blm.takeout.controller;

import com.blm.takeout.entity.Item;
import com.blm.takeout.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Item>> searchItems(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Item> items = itemService.searchItems(keyword, pageRequest);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<Page<Item>> getItemsByShopId(
            @PathVariable Integer shopId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Item> items = itemService.getItemsByShopId(shopId, pageRequest);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{itemId}")
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