package com.blm.takeout.controller;

import com.blm.takeout.service.ShopRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shop-recommendations")
public class ShopRecommendationController {

    @Autowired
    private ShopRecommendationService shopRecommendationService;

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getShopRecommendations(@PathVariable Integer userId) {
        try {
            List<Map<String, Object>> recommendations = shopRecommendationService.getShopRecommendations(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("data", recommendations);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "获取店铺推荐失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/{userId}/generate")
    public ResponseEntity<Map<String, Object>> generateShopRecommendations(
            @PathVariable Integer userId,
            @RequestBody Map<String, Object> preferences) {
        try {
            shopRecommendationService.generateShopRecommendations(userId, preferences);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("message", "店铺推荐生成成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "生成店铺推荐失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
} 