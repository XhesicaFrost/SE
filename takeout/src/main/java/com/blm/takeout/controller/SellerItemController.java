package com.blm.takeout.controller;

import com.blm.takeout.common.ApiResponse;
import com.blm.takeout.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/seller/item")
@RequiredArgsConstructor
public class SellerItemController {
    private final ItemService itemService;

    @PostMapping("/register")
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
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @GetMapping
    public ApiResponse<?> getItem(@RequestParam Integer id) {
        try {
            var item = itemService.getItemById(id);
            return ApiResponse.success(Map.of(
                "itemName", item.getName(),
                "itemPrice", item.getPrice(),
                "itemImage", item.getImage(),
                "itemDescription", item.getDescription()
            ));
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @PostMapping("/edit")
    public ApiResponse<?> editItem(
            @RequestParam Integer itemId,
            @RequestParam String itemName,
            @RequestParam Double itemPrice,
            @RequestParam(required = false) MultipartFile itemImage,
            @RequestParam(required = false) String itemDescription) {
        try {
            itemService.editItem(itemId, itemName, itemDescription, itemPrice, itemImage);
            return ApiResponse.success(Map.of("status", "success"));
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @PostMapping("/offline")
    public ApiResponse<?> offlineItem(@RequestBody Map<String, Integer> params) {
        try {
            Integer itemId = params.get("itemId");
            Integer sellerId = params.get("sellerId");
            if (itemId == null || sellerId == null) {
                return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "缺少必要参数");
            }
            itemService.offlineItem(itemId, sellerId);
            return ApiResponse.success(Map.of("status", "success"));
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @PostMapping("/online")
    public ApiResponse<?> onlineItem(@RequestBody Map<String, Integer> params) {
        try {
            Integer itemId = params.get("itemId");
            Integer sellerId = params.get("sellerId");
            if (itemId == null || sellerId == null) {
                return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "缺少必要参数");
            }
            itemService.onlineItem(itemId, sellerId);
            return ApiResponse.success(Map.of("status", "success"));
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }
} 