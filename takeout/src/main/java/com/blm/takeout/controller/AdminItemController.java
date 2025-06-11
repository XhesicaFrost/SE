package com.blm.takeout.controller;

import com.blm.takeout.common.ApiResponse;
import com.blm.takeout.entity.Item;
import com.blm.takeout.service.ItemService;
import com.blm.takeout.repository.ItemRepository;
import com.blm.takeout.util.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@RestController
@RequestMapping("/admin")
public class AdminItemController {
    private final ItemService itemService;
    private final ItemRepository itemRepository;

    public AdminItemController(ItemService itemService, ItemRepository itemRepository) {
        this.itemService = itemService;
        this.itemRepository = itemRepository;
    }

    @GetMapping("/items")
    public ApiResponse<?> getAllItems() {
        try {
            List<Item> items = itemService.getAllItems();
            List<Map<String, Object>> itemList = new ArrayList<>();
            
            for (Item item : items) {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("id", item.getId());
                itemMap.put("name", item.getName());
                itemMap.put("price", item.getPrice());
                itemMap.put("description", item.getDescription());
                itemMap.put("status", item.getStatus());
                itemMap.put("shopId", item.getShopId());
                
                // 将图片转换为base64
                if (item.getImage() != null && !item.getImage().isEmpty()) {
                    try {
                        String base64Image = FileUtils.convertImageToBase64(item.getImage());
                        itemMap.put("image", base64Image);
                    } catch (IOException e) {
                        e.printStackTrace();
                        itemMap.put("image", "");
                    }
                } else {
                    itemMap.put("image", "");
                }
                
                itemList.add(itemMap);
            }
            
            return ApiResponse.success(itemList);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @GetMapping("/item/{id}")
    public ApiResponse<?> getItemById(@PathVariable Integer id) {
        try {
            Item item = itemService.getItemById(id);
            if (item == null) {
                return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "商品不存在");
            }
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("id", item.getId());
            itemMap.put("name", item.getName());
            itemMap.put("price", item.getPrice());
            itemMap.put("description", item.getDescription());
            itemMap.put("shopId", item.getShopId());
            itemMap.put("isActive", item.getStatus() == Item.Status.正常);
            // 将图片转换为base64
            if (item.getImage() != null && !item.getImage().isEmpty()) {
                try {
                    String base64Image = FileUtils.convertImageToBase64(item.getImage());
                    itemMap.put("image", base64Image);
                } catch (IOException e) {
                    e.printStackTrace();
                    itemMap.put("image", "");
                }
            } else {
                itemMap.put("image", "");
            }
            return ApiResponse.success(itemMap);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/item/edit")
    public ApiResponse<?> editItem(
            @RequestParam("itemId") Integer itemId,
            @RequestParam("name") String name,
            @RequestParam("price") Double price,
            @RequestParam("description") String description,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            itemService.editItem(itemId, name, description, price, image);
            return ApiResponse.success(true);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/item/status")
    public ApiResponse<?> toggleItemStatus(
            @RequestParam("itemId") Integer itemId,
            @RequestParam("status") String status) {
        try {
            Item item = itemService.getItemById(itemId);
            if (item == null) {
                return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "商品不存在");
            }

            if ("enable".equals(status)) {
                item.setStatus(Item.Status.正常);
            } else if ("disable".equals(status)) {
                item.setStatus(Item.Status.审批中);
            } else {
                return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "无效的状态值");
            }

            itemRepository.save(item);
            return ApiResponse.success(true);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }
} 