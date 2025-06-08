package com.blm.takeout.controller;

import com.blm.takeout.entity.Recommendation;
import com.blm.takeout.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getRecommendations(@PathVariable Integer userId) {
        try {
            List<Recommendation> recommendations = recommendationService.getRecommendationsForUser(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("data", recommendations);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "获取推荐失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/generate/{userId}")
    public ResponseEntity<?> generateRecommendations(
            @PathVariable Integer userId,
            @RequestBody Map<Integer, Map<Integer, Double>> userRatings) {
        try {
            recommendationService.generateRecommendations(userId, userRatings);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("message", "推荐生成成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "推荐生成失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/shops/filter")
    public ResponseEntity<?> filterShops(
            @RequestParam(required = false) String shopType,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxRating,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer maxDeliveryTime) {
        
        try {
            Map<String, Double> location = null;
            if (latitude != null && longitude != null) {
                location = Map.of("latitude", latitude, "longitude", longitude);
            }
            
            List<Map<String, Object>> shops = recommendationService.filterShops(
                shopType,
                location,
                minRating,
                maxRating,
                minPrice,
                maxPrice,
                maxDeliveryTime
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("data", shops);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "筛选店铺失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
} 