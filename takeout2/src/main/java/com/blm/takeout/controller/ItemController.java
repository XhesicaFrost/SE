package com.blm.takeout.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.blm.takeout.common.ApiResponse;
import com.blm.takeout.service.ItemService;
import com.blm.takeout.util.FileUtils;
import com.blm.takeout.entity.Item;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seller/item")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public ApiResponse<?> getItemInfo(@RequestParam Integer id) {
        try {
            Item item = itemService.getItemById(id);
            if (item != null) {
                String base64Image = FileUtils.convertImageToBase64(item.getImage());
                return ApiResponse.success(Map.of(
                    "itemName", item.getName(),
                    "itemPrice", item.getPrice(),
                    "itemImage", base64Image
                ));
            } else {
                return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "商品不存在");
            }
        } catch (Exception e) {
                return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/register")
    public ApiResponse<?> registerItem(@RequestParam String itemName,
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
}