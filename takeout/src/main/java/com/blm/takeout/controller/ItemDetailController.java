package com.blm.takeout.controller;

import com.blm.takeout.service.ItemDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/items")
public class ItemDetailController {

    @Autowired
    private ItemDetailService itemDetailService;

    @GetMapping("/{itemId}")
    public ResponseEntity<Map<String, Object>> getItemDetail(@PathVariable Integer itemId) {
        try {
            Map<String, Object> itemDetail = itemDetailService.getItemDetail(itemId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("data", itemDetail);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "获取商品详情失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/{itemId}/reviews")
    public ResponseEntity<Map<String, Object>> getItemReviews(
            @PathVariable Integer itemId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        try {
            Map<String, Object> reviews = itemDetailService.getItemReviews(itemId, page, size);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("data", reviews);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "获取商品评价失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
} 