package com.blm.takeout.service;

import com.blm.takeout.entity.ShopRecommendation;
import com.blm.takeout.entity.User;
import com.blm.takeout.entity.Shop;
import com.blm.takeout.entity.UserShopPreference;
import com.blm.takeout.repository.ShopRecommendationRepository;
import com.blm.takeout.repository.UserShopPreferenceRepository;
import com.blm.takeout.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShopRecommendationService {

    private final ShopRecommendationRepository shopRecommendationRepository;
    private final UserShopPreferenceRepository userShopPreferenceRepository;
    private final ShopRepository shopRepository;

    @Autowired
    public ShopRecommendationService(
            ShopRecommendationRepository shopRecommendationRepository,
            UserShopPreferenceRepository userShopPreferenceRepository,
            ShopRepository shopRepository) {
        this.shopRecommendationRepository = shopRecommendationRepository;
        this.userShopPreferenceRepository = userShopPreferenceRepository;
        this.shopRepository = shopRepository;
    }

    @Transactional
    public void generateShopRecommendations(Integer userId, Map<String, Object> preferences) {
        // 删除旧的推荐
        shopRecommendationRepository.deleteByUser_userid(userId);
        
        // 获取用户偏好
        UserShopPreference userPreference = userShopPreferenceRepository.findByUser_userid(userId)
            .orElseGet(() -> {
                UserShopPreference newPreference = new UserShopPreference();
                User user = new User();
                user.setUserid(userId);
                newPreference.setUser(user);
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
        
        // 获取所有店铺
        List<Shop> shops = shopRepository.findAll();
        
        // 计算推荐分数
        List<ShopRecommendation> recommendations = shops.stream()
            .map(shop -> {
                double score = calculateShopScore(shop, userPreference, location);
                if (score > 0) {
                    ShopRecommendation rec = new ShopRecommendation();
                    User user = new User();
                    user.setUserid(userId);
                    rec.setUser(user);
                    rec.setShop(shop);
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

    private double calculateShopScore(Shop shop, UserShopPreference preference, Map<String, Double> location) {
        double score = 0.0;
        
        // 店铺类型匹配度
        if (preference.getPreferredTypes() != null && 
            preference.getPreferredTypes().contains(shop.getType())) {
            score += 0.4;
        }
        
        // 价格范围匹配度
        if (preference.getMinPrice() != null && preference.getMaxPrice() != null) {
            if (shop.getMinPrice() >= preference.getMinPrice() && 
                shop.getMaxPrice() <= preference.getMaxPrice()) {
                score += 0.3;
            } else if (shop.getMinPrice() <= preference.getMaxPrice() && 
                      shop.getMaxPrice() >= preference.getMinPrice()) {
                score += 0.15;
            }
        }
        
        // 配送时间匹配度
        if (preference.getMaxDeliveryTime() != null && 
            shop.getDeliveryTime() <= preference.getMaxDeliveryTime()) {
            score += 0.2;
        }
        
        // 距离匹配度
        if (location != null) {
            double distance = calculateDistance(
                location.get("latitude"),
                location.get("longitude"),
                shop.getLatitude(),
                shop.getLongitude()
            );
            if (distance <= 5.0) {
                score += 0.1 * (1 - distance / 5.0);
            }
        }
        
        return score;
    }

    private String generateRecommendationReason(Shop shop, UserShopPreference preference) {
        List<String> reasons = new ArrayList<>();
        
        if (preference.getPreferredTypes() != null && 
            preference.getPreferredTypes().contains(shop.getType())) {
            reasons.add("符合您喜欢的店铺类型");
        }
        
        if (preference.getMinPrice() != null && preference.getMaxPrice() != null &&
            shop.getMinPrice() >= preference.getMinPrice() && 
            shop.getMaxPrice() <= preference.getMaxPrice()) {
            reasons.add("价格在您的预算范围内");
        }
        
        if (preference.getMaxDeliveryTime() != null && 
            shop.getDeliveryTime() <= preference.getMaxDeliveryTime()) {
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

    public List<Map<String, Object>> getUserShopRecommendations(Integer userId) {
        List<ShopRecommendation> recommendations = shopRecommendationRepository.findByUser_useridOrderByScoreDesc(userId);
        
        return recommendations.stream()
            .map(recommendation -> {
                Shop shop = recommendation.getShop();
                return Map.of(
                    "id", shop.getId(),
                    "name", shop.getName(),
                    "type", shop.getType(),
                    "rating", shop.getRating(),
                    "minPrice", shop.getMinPrice(),
                    "maxPrice", shop.getMaxPrice(),
                    "deliveryTime", shop.getDeliveryTime(),
                    "location", Map.of(
                        "latitude", shop.getLatitude(),
                        "longitude", shop.getLongitude()
                    ),
                    "score", recommendation.getScore(),
                    "recommendationReason", recommendation.getRecommendationReason()
                );
            })
            .collect(Collectors.toList());
    }
} 