package com.blm.takeout.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.blm.takeout.entity.Promotion;
import com.blm.takeout.repository.PromotionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PromotionService {
    private final PromotionRepository promotionRepository;

    public void registerPromotion(String promotionName, Double full, Double minus, String startTime, String endTime, Integer sellerId) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(startTime, formatter);
        LocalDateTime end = LocalDateTime.parse(endTime, formatter);

        Promotion promotion = new Promotion();
        promotion.setPromotionName(promotionName);
        promotion.setFull(full);
        promotion.setMinus(minus);
        promotion.setStartTime(start);
        promotion.setEndTime(end);
        promotion.setSellerId(sellerId);
        promotionRepository.save(promotion);
    }

    public void editPromotion(Integer promotionId, String promotionName, Double full, Double minus, String startTime, String endTime, Integer sellerId) throws Exception {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new Exception("促销活动不存在"));

        if (!promotion.getSellerId().equals(sellerId)) {
            throw new Exception("商家ID不匹配，无法编辑促销活动");
        }

        promotion.setPromotionName(promotionName);
        promotion.setFull(full);
        promotion.setMinus(minus);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        promotion.setStartTime(LocalDateTime.parse(startTime, formatter));
        promotion.setEndTime(LocalDateTime.parse(endTime, formatter));

        promotionRepository.save(promotion);
    }
}
