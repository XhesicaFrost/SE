package com.blm.takeout.controller;

import com.blm.takeout.common.ApiResponse;
import com.blm.takeout.entity.Item;
import com.blm.takeout.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/approval")
@RequiredArgsConstructor
public class ApprovalController {
    private final ItemService itemService;

    @GetMapping("/item")
    public ApiResponse<?> getPendingItems(@RequestParam Integer sellerId) {
        try {
            List<Item> items = itemService.getItemsByShopId(sellerId);
            List<Map<String, Object>> pendingItems = items.stream()
                .filter(item -> item.getStatus() == Item.Status.审批中)
                .map(item -> {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("id", item.getId());
                    itemMap.put("name", item.getName());
                    itemMap.put("price", item.getPrice());
                    itemMap.put("image", item.getImage());
                    itemMap.put("description", item.getDescription());
                    itemMap.put("status", item.getStatus().toString());
                    return itemMap;
                })
                .collect(Collectors.toList());
            return ApiResponse.success(pendingItems);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }
} 