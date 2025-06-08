package com.blm.takeout.service;

import com.blm.takeout.entity.Recommendation;
import com.blm.takeout.repository.RecommendationRepository;
import com.blm.takeout.util.CollaborativeFiltering;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Transactional
    public void generateRecommendations(Integer userId, Map<Integer, Map<Integer, Double>> userRatings) {
        // 删除旧的推荐
        recommendationRepository.deleteByUserId(userId);
        
        // 获取新的推荐
        List<Map.Entry<Integer, Double>> recommendations = 
            CollaborativeFiltering.getRecommendations(userRatings, userId, 10);
        
        // 保存推荐结果
        List<Recommendation> recommendationEntities = recommendations.stream()
            .map(entry -> {
                Recommendation rec = new Recommendation();
                rec.setUserId(userId);
                rec.setItemId(entry.getKey());
                rec.setScore(entry.getValue());
                return rec;
            })
            .collect(Collectors.toList());
        
        recommendationRepository.saveAll(recommendationEntities);
    }

    public List<Recommendation> getRecommendationsForUser(Integer userId) {
        return recommendationRepository.findTopRecommendationsByUserId(userId);
    }

    /**
     * 根据筛选条件获取店铺列表
     * @param shopType 店铺类型
     * @param location 位置信息（经纬度）
     * @param minRating 最低评分
     * @param maxRating 最高评分
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @param maxDeliveryTime 最大配送时间（分钟）
     * @return 筛选后的店铺列表
     */
    public List<Map<String, Object>> filterShops(
            String shopType,
            Map<String, Double> location,
            Double minRating,
            Double maxRating,
            Double minPrice,
            Double maxPrice,
            Integer maxDeliveryTime) {
        
        // 这里需要调用数据库查询，暂时返回模拟数据
        List<Map<String, Object>> shops = new ArrayList<>();
        
        // 模拟数据
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
        
        // 应用筛选条件
        return shops.stream()
            .filter(shop -> {
                // 店铺类型筛选
                if (shopType != null && !shopType.isEmpty()) {
                    if (!shop.get("type").equals(shopType)) {
                        return false;
                    }
                }
                
                // 评分筛选
                Double rating = (Double) shop.get("rating");
                if (minRating != null && rating < minRating) {
                    return false;
                }
                if (maxRating != null && rating > maxRating) {
                    return false;
                }
                
                // 价格筛选
                Double minShopPrice = (Double) shop.get("minPrice");
                Double maxShopPrice = (Double) shop.get("maxPrice");
                if (minPrice != null && maxShopPrice < minPrice) {
                    return false;
                }
                if (maxPrice != null && minShopPrice > maxPrice) {
                    return false;
                }
                
                // 配送时间筛选
                Integer deliveryTime = (Integer) shop.get("deliveryTime");
                if (maxDeliveryTime != null && deliveryTime > maxDeliveryTime) {
                    return false;
                }
                
                // 位置筛选（如果提供了位置信息）
                if (location != null) {
                    @SuppressWarnings("unchecked")
                    Map<String, Double> shopLocation = (Map<String, Double>) shop.get("location");
                    double distance = calculateDistance(
                        location.get("latitude"),
                        location.get("longitude"),
                        shopLocation.get("latitude"),
                        shopLocation.get("longitude")
                    );
                    // 假设5公里为最大配送范围
                    if (distance > 5.0) {
                        return false;
                    }
                }
                
                return true;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * 计算两点之间的距离（使用Haversine公式）
     * @param lat1 第一个点的纬度
     * @param lon1 第一个点的经度
     * @param lat2 第二个点的纬度
     * @param lon2 第二个点的经度
     * @return 距离（公里）
     */
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