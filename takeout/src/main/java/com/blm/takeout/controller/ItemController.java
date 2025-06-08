package com.blm.takeout.controller;

import com.blm.takeout.entity.Item;
import com.blm.takeout.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.blm.takeout.common.ApiResponse;
import com.blm.takeout.util.FileUtils;
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

    @GetMapping("/items/search")
    public ResponseEntity<Page<Item>> searchItems(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Item> items = itemService.searchItems(keyword, pageRequest);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<Map<String, Object>> getItemDetails(@PathVariable Integer id) {
        try {
            Map<String, Object> itemDetails = itemService.getItemDetails(id);
            return ResponseEntity.ok(itemDetails);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/items/register")
    public ApiResponse<?> registerItem(
            @RequestParam String itemName,
            @RequestParam MultipartFile itemImage,
            @RequestParam Double itemPrice,
            @RequestParam Integer sellerId,
            @RequestParam(required = false) String itemDescription) {
        try {
            itemService.registerItem(itemName, itemImage, itemPrice, sellerId, itemDescription);
            return ApiResponse.success(Map.of("status", "success"));
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PostMapping("/items/edit")
    public ApiResponse<?> editItem(
            @RequestParam Integer itemId,
            @RequestParam String itemName,
            @RequestParam Double itemPrice,
            @RequestParam(required = false) MultipartFile itemImage,
            @RequestParam(required = false) String itemDescription) {
        try {
            itemService.editItem(itemId, itemName, itemPrice, itemImage, itemDescription);
            return ApiResponse.success(Map.of("status", "success"));
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
} 
