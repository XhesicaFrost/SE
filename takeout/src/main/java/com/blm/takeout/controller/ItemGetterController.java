package com.blm.takeout.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blm.takeout.common.ApiResponse;
import com.blm.takeout.service.ItemService;
import com.blm.takeout.entity.Item;
import com.blm.takeout.util.FileUtils;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/item")
public class ItemGetterController {
    private final ItemService itemService;

    @GetMapping
    public ApiResponse<?> getNormalAndOffShelfItems(@RequestParam Integer sellerId) {
        try {
            List<Item> items = itemService.getNormalAndOffShelfItemsBySeller(sellerId);
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
                map.put("status", item.getStatus());
                return map;
            }).collect(Collectors.toList());
            return ApiResponse.success(itemList);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }
}
