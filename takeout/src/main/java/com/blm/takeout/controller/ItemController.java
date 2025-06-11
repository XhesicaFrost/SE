package com.blm.takeout.controller;

import com.blm.takeout.entity.Item;
import com.blm.takeout.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.blm.takeout.common.ApiResponse;
import com.blm.takeout.util.FileUtils;
import java.io.IOException;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public ApiResponse<?> getItemsByShop(@RequestParam Integer shopId) {
        try {
            List<Item> items = itemService.getItemsByShopId(shopId);
            List<Map<String, Object>> itemList = items.stream().map(item -> {
                Map<String, Object> map = new HashMap<>();
                try {
                    String base64Image = FileUtils.convertImageToBase64(item.getImage());
                    map.put("image", base64Image); 
                } catch (IOException e) {
                    map.put("image", null);
                }
                map.put("id", item.getId());
                map.put("name", item.getName());
                map.put("price", item.getPrice());
                map.put("sales", item.getSales());
                map.put("status", item.getStatus().toString());
                map.put("description", item.getDescription());
                map.put("rating", item.getRating());
                return map;
            }).collect(Collectors.toList());
            return ApiResponse.success(itemList);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Integer id) {
        Item item = itemService.getItemById(id);
        return item != null ? ResponseEntity.ok(item) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> registerItem(
            @RequestParam String itemName,
            @RequestParam MultipartFile itemImage,
            @RequestParam Double itemPrice,
            @RequestParam Integer shopId,
            @RequestParam(required = false) String itemDescription) {
        try {
            itemService.registerItem(itemName, itemImage, itemPrice, shopId, itemDescription);
            return ResponseEntity.ok(Map.of("status", "success"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "fail", "message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editItem(
            @PathVariable Integer id,
            @RequestParam String itemName,
            @RequestParam Double itemPrice,
            @RequestParam(required = false) MultipartFile itemImage,
            @RequestParam(required = false) String itemDescription) {
        try {
            itemService.editItem(id, itemName, itemDescription, itemPrice, itemImage);
            return ResponseEntity.ok(Map.of("status", "success"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "fail", "message", e.getMessage()));
        }
    }

    @PostMapping("/{id}/offline")
    public ResponseEntity<?> offlineItem(
            @PathVariable Integer id,
            @RequestParam Integer shopId) {
        try {
            itemService.offlineItem(id, shopId);
            return ResponseEntity.ok(Map.of("status", "success"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "fail", "message", e.getMessage()));
        }
    }

    @PostMapping("/{id}/online")
    public ResponseEntity<?> onlineItem(
            @PathVariable Integer id,
            @RequestParam Integer shopId) {
        try {
            itemService.onlineItem(id, shopId);
            return ResponseEntity.ok(Map.of("status", "success"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "fail", "message", e.getMessage()));
        }
    }
}
