package com.blm.takeout.util;

import java.util.*;
import java.util.stream.Collectors;

public class CollaborativeFiltering {
    
    // 计算两个用户之间的相似度（使用皮尔逊相关系数）
    public static double calculateSimilarity(Map<Long, Double> user1Ratings, Map<Long, Double> user2Ratings) {
        List<Long> commonItems = new ArrayList<>();
        for (Long itemId : user1Ratings.keySet()) {
            if (user2Ratings.containsKey(itemId)) {
                commonItems.add(itemId);
            }
        }
        
        if (commonItems.isEmpty()) {
            return 0.0;
        }
        
        double sum1 = 0, sum2 = 0, sum1Sq = 0, sum2Sq = 0, pSum = 0;
        int n = commonItems.size();
        
        for (Long itemId : commonItems) {
            double r1 = user1Ratings.get(itemId);
            double r2 = user2Ratings.get(itemId);
            
            sum1 += r1;
            sum2 += r2;
            sum1Sq += r1 * r1;
            sum2Sq += r2 * r2;
            pSum += r1 * r2;
        }
        
        double num = pSum - (sum1 * sum2 / n);
        double den = Math.sqrt((sum1Sq - sum1 * sum1 / n) * (sum2Sq - sum2 * sum2 / n));
        
        if (den == 0) return 0.0;
        
        return num / den;
    }
    
    // 获取推荐商品
    public static List<Map.Entry<Integer, Double>> getRecommendations(
            Map<Integer, Map<Integer, Double>> userRatings,
            Integer userId,
            int numRecommendations) {
        
        // 计算用户相似度
        Map<Integer, Double> similarities = calculateUserSimilarities(userRatings, userId);
        
        // 获取目标用户未评分的商品
        Set<Integer> userRatedItems = userRatings.get(userId).keySet();
        Set<Integer> allItems = new HashSet<>();
        userRatings.values().forEach(ratings -> allItems.addAll(ratings.keySet()));
        Set<Integer> unratedItems = new HashSet<>(allItems);
        unratedItems.removeAll(userRatedItems);
        
        // 预测评分
        Map<Integer, Double> predictions = new HashMap<>();
        for (Integer itemId : unratedItems) {
            double prediction = predictRating(userRatings, similarities, userId, itemId);
            predictions.put(itemId, prediction);
        }
        
        // 排序并返回推荐结果
        return predictions.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(numRecommendations)
                .collect(Collectors.toList());
    }
    
    private static Map<Integer, Double> calculateUserSimilarities(
            Map<Integer, Map<Integer, Double>> userRatings,
            Integer targetUserId) {
        
        Map<Integer, Double> similarities = new HashMap<>();
        Map<Integer, Double> targetUserRatings = userRatings.get(targetUserId);
        
        for (Map.Entry<Integer, Map<Integer, Double>> entry : userRatings.entrySet()) {
            Integer otherUserId = entry.getKey();
            if (!otherUserId.equals(targetUserId)) {
                double similarity = calculatePearsonCorrelation(
                    targetUserRatings,
                    entry.getValue()
                );
                similarities.put(otherUserId, similarity);
            }
        }
        
        return similarities;
    }
    
    private static double calculatePearsonCorrelation(
            Map<Integer, Double> ratings1,
            Map<Integer, Double> ratings2) {
        
        // 找到共同评分的商品
        Set<Integer> commonItems = new HashSet<>(ratings1.keySet());
        commonItems.retainAll(ratings2.keySet());
        
        if (commonItems.isEmpty()) {
            return 0.0;
        }
        
        int n = commonItems.size();
        double sum1 = 0.0, sum2 = 0.0, sum1Sq = 0.0, sum2Sq = 0.0, pSum = 0.0;
        
        for (Integer itemId : commonItems) {
            double r1 = ratings1.get(itemId);
            double r2 = ratings2.get(itemId);
            
            sum1 += r1;
            sum2 += r2;
            sum1Sq += r1 * r1;
            sum2Sq += r2 * r2;
            pSum += r1 * r2;
        }
        
        double num = pSum - (sum1 * sum2 / n);
        double den = Math.sqrt((sum1Sq - sum1 * sum1 / n) * (sum2Sq - sum2 * sum2 / n));
        
        if (den == 0) {
            return 0.0;
        }
        
        return num / den;
    }
    
    private static double predictRating(
            Map<Integer, Map<Integer, Double>> userRatings,
            Map<Integer, Double> similarities,
            Integer userId,
            Integer itemId) {
        
        double weightedSum = 0.0;
        double similaritySum = 0.0;
        
        for (Map.Entry<Integer, Double> entry : similarities.entrySet()) {
            Integer otherUserId = entry.getKey();
            double similarity = entry.getValue();
            
            if (similarity > 0) {
                Map<Integer, Double> otherUserRatings = userRatings.get(otherUserId);
                if (otherUserRatings.containsKey(itemId)) {
                    weightedSum += similarity * otherUserRatings.get(itemId);
                    similaritySum += similarity;
                }
            }
        }
        
        if (similaritySum == 0) {
            return 0.0;
        }
        
        return weightedSum / similaritySum;
    }
} 