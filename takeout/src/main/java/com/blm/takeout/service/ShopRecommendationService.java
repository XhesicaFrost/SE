package com.blm.takeout.service;

import com.blm.takeout.entity.ShopRecommendation;
import com.blm.takeout.entity.UserShopPreference;
import com.blm.takeout.repository.ShopRecommendationRepository;
import com.blm.takeout.repository.UserShopPreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShopRecommendationService {

    @Autowired
    private ShopRecommendationRepository shopRecommendationRepository;

    @Autowired
    private UserShopPreferenceRepository userShopPreferenceRepository;

    @Transactional
    public void generateShopRecommendations(Integer userId, Map<String, Object> preferences) {
        // 删除旧的推荐
        shopRecommendationRepository.deleteByUserId(userId);
        
        // 获取用户偏好
        UserShopPreference userPreference = userShopPreferenceRepository.findByUserId(userId)
            .orElseGet(() -> {
                UserShopPreference newPreference = new UserShopPreference();
                newPreference.setUserId(userId);
                newPreference.setCreatedAt(LocalDateTime.now());
                newPreference.setUpdatedAt(LocalDateTime.now());
                return newPreference;
            });
        
        // 更新用户偏好
        @SuppressWarnings("unchecked")
        Map<String, Object> userPrefs = (Map<String, Object>) preferences.get("userPreferences");
        if (userPrefs != null) {
            @SuppressWarnings("unchecked")
            List<String> preferredTypes = (List<String>) userPrefs.get("preferredTypes");
            if (preferredTypes != null) {
                userPreference.setPreferredTypes(preferredTypes);
            }
            
            @SuppressWarnings("unchecked")
            Map<String, Double> priceRange = (Map<String, Double>) userPrefs.get("priceRange");
            if (priceRange != null) {
                userPreference.setMinPrice(priceRange.get("min"));
                userPreference.setMaxPrice(priceRange.get("max"));
            }
            
            userPreference.setMaxDeliveryTime((Integer) userPrefs.get("maxDeliveryTime"));
            userPreference.setUpdatedAt(LocalDateTime.now());
            userShopPreferenceRepository.save(userPreference);
        }
        
        // 获取位置信息
        @SuppressWarnings("unchecked")
        Map<String, Double> location = (Map<String, Double>) preferences.get("location");
        
        // 这里应该调用数据库查询获取符合条件的店铺
        // 暂时使用模拟数据
        List<Map<String, Object>> shops = getMockShops();
        
        // 计算推荐分数
        List<ShopRecommendation> recommendations = shops.stream()
            .map(shop -> {
                double score = calculateShopScore(shop, userPreference, location);
                if (score > 0) {
                    ShopRecommendation rec = new ShopRecommendation();
                    rec.setUserId(userId);
                    rec.setShopId((Integer) shop.get("id"));
                    rec.setScore(score);
                    rec.setRecommendationReason(generateRecommendationReason(shop, userPreference));
                    rec.setCreatedAt(LocalDateTime.now());
                    return rec;
                }
                return null;
            })
            .filter(Objects::nonNull)
            .sorted(Comparator.comparing(ShopRecommendation::getScore).reversed())
            .limit(10)
            .collect(Collectors.toList());
        
        shopRecommendationRepository.saveAll(recommendations);
    }

    public List<Map<String, Object>> getShopRecommendations(Integer userId) {
        List<ShopRecommendation> recommendations = shopRecommendationRepository.findTopRecommendationsByUserId(userId);
        // 这里应该从数据库获取店铺详细信息
        // 暂时返回模拟数据
        return recommendations.stream()
            .map(rec -> {
                Map<String, Object> shop = new HashMap<>();
                shop.put("id", rec.getShopId());
                shop.put("name", "示例店铺" + rec.getShopId());
                shop.put("type", "中餐");
                shop.put("rating", 4.5);
                shop.put("minPrice", 20.0);
                shop.put("maxPrice", 100.0);
                shop.put("deliveryTime", 30);
                shop.put("location", Map.of("latitude", 39.9042, "longitude", 116.4074));
                shop.put("score", rec.getScore());
                shop.put("recommendationReason", rec.getRecommendationReason());
                return shop;
            })
            .collect(Collectors.toList());
    }

    private List<Map<String, Object>> getMockShops() {
        List<Map<String, Object>> shops = new ArrayList<>();
        
        Map<String, Object> shop1 = new HashMap<>();
        shop1.put("id", 1);
        shop1.put("name", "示例店铺1");
        shop1.put("type", "中餐");
        shop1.put("rating", 4.5);
        shop1.put("minPrice", 20.0);
        shop1.put("maxPrice", 100.0);
        shop1.put("deliveryTime", 30);
        shop1.put("location", Map.of("latitude", 39.9042, "longitude", 116.4074));
        shops.add(shop1);
        
        return shops;
    }

    private double calculateShopScore(Map<String, Object> shop, UserShopPreference preference, Map<String, Double> location) {
        double score = 0.0;
        
        // 店铺类型匹配度
        if (preference.getPreferredTypes() != null && 
            preference.getPreferredTypes().contains(shop.get("type"))) {
            score += 0.4;
        }
        
        // 价格范围匹配度
        double minPrice = (Double) shop.get("minPrice");
        double maxPrice = (Double) shop.get("maxPrice");
        if (preference.getMinPrice() != null && preference.getMaxPrice() != null) {
            if (minPrice >= preference.getMinPrice() && maxPrice <= preference.getMaxPrice()) {
                score += 0.3;
            } else if (minPrice <= preference.getMaxPrice() && maxPrice >= preference.getMinPrice()) {
                score += 0.15;
            }
        }
        
        // 配送时间匹配度
        Integer deliveryTime = (Integer) shop.get("deliveryTime");
        if (preference.getMaxDeliveryTime() != null && 
            deliveryTime <= preference.getMaxDeliveryTime()) {
            score += 0.2;
        }
        
        // 距离匹配度
        if (location != null) {
            @SuppressWarnings("unchecked")
            Map<String, Double> shopLocation = (Map<String, Double>) shop.get("location");
            double distance = calculateDistance(
                location.get("latitude"),
                location.get("longitude"),
                shopLocation.get("latitude"),
                shopLocation.get("longitude")
            );
            if (distance <= 5.0) {
                score += 0.1 * (1 - distance / 5.0);
            }
        }
        
        return score;
    }

    private String generateRecommendationReason(Map<String, Object> shop, UserShopPreference preference) {
        List<String> reasons = new ArrayList<>();
        
        if (preference.getPreferredTypes() != null && 
            preference.getPreferredTypes().contains(shop.get("type"))) {
            reasons.add("符合您喜欢的店铺类型");
        }
        
        double minPrice = (Double) shop.get("minPrice");
        double maxPrice = (Double) shop.get("maxPrice");
        if (preference.getMinPrice() != null && preference.getMaxPrice() != null &&
            minPrice >= preference.getMinPrice() && maxPrice <= preference.getMaxPrice()) {
            reasons.add("价格在您的预算范围内");
        }
        
        Integer deliveryTime = (Integer) shop.get("deliveryTime");
        if (preference.getMaxDeliveryTime() != null && 
            deliveryTime <= preference.getMaxDeliveryTime()) {
            reasons.add("配送时间符合您的要求");
        }
        
        return String.join("，", reasons);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 地球半径（公里）
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
} 