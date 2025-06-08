package com.blm.takeout.controller;

import com.blm.takeout.service.ShopRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserShopPreferenceController {
    
    private final ShopRecommendationService shopRecommendationService;

    @Autowired
    public UserShopPreferenceController(ShopRecommendationService shopRecommendationService) {
        this.shopRecommendationService = shopRecommendationService;
    }

    @GetMapping("/shop-preferences")
    public Map<String, Object> getUserShopPreferences(@RequestParam Integer userId) {
        try {
            return Map.of(
                "code", 200,
                "success", true,
                "data", shopRecommendationService.getUserShopPreferences(userId)
            );
        } catch (Exception e) {
            return Map.of(
                "code", 500,
                "success", false,
                "message", "获取用户店铺偏好失败：" + e.getMessage()
            );
        }
    }

    @PostMapping("/shop-preferences")
    public Map<String, Object> updateUserShopPreferences(
            @RequestParam Integer userId,
            @RequestBody Map<String, Object> preferences) {
        try {
            shopRecommendationService.updateUserShopPreferences(userId, preferences);
            return Map.of(
                "code", 200,
                "success", true,
                "message", "店铺偏好更新成功"
            );
        } catch (Exception e) {
            return Map.of(
                "code", 500,
                "success", false,
                "message", "更新用户店铺偏好失败：" + e.getMessage()
            );
        }
    }
} 