package com.blm.takeout.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blm.takeout.entity.Promotion;
import com.blm.takeout.repository.PromotionRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionService {
    private final PromotionRepository promotionRepository;

    @Transactional
    public void registerPromotion(String promotionName, Double full, Double minus, String startTime, String endTime, Integer sellerId) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
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

    @Transactional
    public void editPromotion(Integer promotionId, String promotionName, Double full, Double minus, String startTime, String endTime, Integer sellerId) throws Exception {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new Exception("促销活动不存在"));

        if (!promotion.getSellerId().equals(sellerId)) {
            throw new Exception("商家ID不匹配，无法编辑促销活动");
        }

        promotion.setPromotionName(promotionName);
        promotion.setFull(full);
        promotion.setMinus(minus);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        promotion.setStartTime(LocalDateTime.parse(startTime, formatter));
        promotion.setEndTime(LocalDateTime.parse(endTime, formatter));

        promotionRepository.save(promotion);
    }

    public List<Promotion> getPromotionsBySellerId(Integer sellerId) {
        return promotionRepository.findBySellerId(sellerId);
    }

    public void deletePromotion(Integer promotionId) throws Exception {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new Exception("促销活动不存在"));
        promotionRepository.delete(promotion);
    }

    public Promotion getPromotionById(Integer promotionId) throws Exception {
        return promotionRepository.findById(promotionId)
                .orElseThrow(() -> new Exception("促销活动不存在"));
    }
}
