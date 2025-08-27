package com.blm.takeout.controller;

import com.blm.takeout.common.ApiResponse;
import com.blm.takeout.entity.Item;
import com.blm.takeout.service.ItemService;
import com.blm.takeout.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.IOException;

@RestController
@RequestMapping("/approval")
public class ApprovalController {
    private final ItemService itemService;

    @Autowired
    public ApprovalController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/item")
    public ApiResponse<?> getPendingItems(@RequestParam Integer sellerId) {
        try {
            List<Item> items = itemService.getItemsByShopId(sellerId);
            List<Map<String, Object>> result = new ArrayList<>();
            
            for (Item item : items) {
                if (item.getStatus() == Item.Status.审批中) {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("id", item.getId());
                    itemMap.put("name", item.getName());
                    itemMap.put("price", item.getPrice());
                    itemMap.put("description", item.getDescription());
                    itemMap.put("status", item.getStatus());
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
                    result.add(itemMap);
                }
            }
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "获取待审批商品失败");
        }
    }

    @GetMapping("/history")
    public ApiResponse<?> getApprovalHistory(@RequestParam Integer sellerId) {
        try {
            List<Item> items = itemService.getItemsByShopId(sellerId);
            List<Map<String, Object>> result = new ArrayList<>();
            
            for (Item item : items) {
                if (item.getStatus() == Item.Status.正常) {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("id", item.getId());
                    itemMap.put("name", item.getName());
                    itemMap.put("price", item.getPrice());
                    itemMap.put("description", item.getDescription());
                    itemMap.put("status", item.getStatus());
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
                    result.add(itemMap);
                }
            }
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "获取审批历史失败");
        }
    }
} 