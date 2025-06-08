package com.blm.takeout.controller;

import com.blm.takeout.common.ApiResponse;
import com.blm.takeout.service.ItemService;
import com.blm.takeout.entity.Item;
import com.blm.takeout.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ItemsController {
    private final ItemService itemService;

    @GetMapping("/items")
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
} 