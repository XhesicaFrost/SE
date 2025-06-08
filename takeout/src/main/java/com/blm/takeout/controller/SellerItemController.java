package com.blm.takeout.controller;

import com.blm.takeout.common.ApiResponse;
import com.blm.takeout.entity.Item;
import com.blm.takeout.service.ItemService;
import com.blm.takeout.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

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
            Item item = itemService.getItemById(id);
            if (item == null) {
                return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "商品不存在");
            }
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("itemName", item.getName());
            itemMap.put("itemPrice", item.getPrice());
            itemMap.put("itemDescription", item.getDescription());
            // 将图片转换为base64
            if (item.getImage() != null && !item.getImage().isEmpty()) {
                try {
                    String base64Image = FileUtils.convertImageToBase64(item.getImage());
                    itemMap.put("itemImage", base64Image);
                } catch (IOException e) {
                    e.printStackTrace();
                    itemMap.put("itemImage", "");
                }
            } else {
                itemMap.put("itemImage", "");
            }
            return ApiResponse.success(itemMap);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
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