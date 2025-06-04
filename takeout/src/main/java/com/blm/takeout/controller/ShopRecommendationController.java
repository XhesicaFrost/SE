package com.blm.takeout.controller;

import com.blm.takeout.service.ShopRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/recommendations/shops")
public class ShopRecommendationController {
    
    private final ShopRecommendationService shopRecommendationService;

    @Autowired
    public ShopRecommendationController(ShopRecommendationService shopRecommendationService) {
        this.shopRecommendationService = shopRecommendationService;
    }

    @PostMapping("/generate/{userId}")
    public Map<String, Object> generateShopRecommendations(
            @PathVariable Integer userId,
            @RequestBody Map<String, Object> preferences) {
        try {
            shopRecommendationService.generateShopRecommendations(userId, preferences);
            return Map.of(
                "code", 200,
                "success", true,
                "message", "店铺推荐生成成功"
            );
        } catch (Exception e) {
            return Map.of(
                "code", 500,
                "success", false,
                "message", "店铺推荐生成失败：" + e.getMessage()
            );
        }
    }

    @GetMapping("/{userId}")
    public Map<String, Object> getUserShopRecommendations(@PathVariable Integer userId) {
        try {
            return Map.of(
                "code", 200,
                "success", true,
                "data", shopRecommendationService.getUserShopRecommendations(userId)
            );
        } catch (Exception e) {
            return Map.of(
                "code", 500,
                "success", false,
                "message", "获取店铺推荐失败：" + e.getMessage()
            );
        }
    }
} 