package com.blm.takeout.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blm.takeout.common.ApiResponse;
import com.blm.takeout.entity.Promotion;
import com.blm.takeout.service.PromotionService;

import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seller/promotion")
public class PromotionController {
    private final PromotionService promotionService;

    @PostMapping("/register")
    public ApiResponse<?> registerPromotion(@RequestParam String promotionName,
                                            @RequestParam Double full,
                                            @RequestParam Double minus,
                                            @RequestParam String startTime,
                                            @RequestParam String endTime,
                                            @RequestParam Integer sellerId) {
        try {
            promotionService.registerPromotion(promotionName, full, minus, startTime, endTime, sellerId);
            return ApiResponse.success(Map.of("status", "success"));
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @PostMapping("/edit")
    public ApiResponse<?> editPromotion(@RequestParam Integer promotionId,
                                        @RequestParam String promotionName,
                                        @RequestParam Double full,
                                        @RequestParam Double minus,
                                        @RequestParam String startTime,
                                        @RequestParam String endTime,
                                        @RequestParam Integer sellerId) {
        try {
            promotionService.editPromotion(promotionId, promotionName, full, minus, startTime, endTime, sellerId);
            return ApiResponse.success(Map.of("status", "success"));
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @GetMapping
    public ApiResponse<?> getPromotions(@RequestParam Integer sellerId) {
        try {
            List<Promotion> promotions = promotionService.getPromotionsBySellerId(sellerId);
            List<Map<String, Object>> promotionList = promotions.stream().map(promotion -> {
                Map<String, Object> map = new HashMap<>();
                map.put("promotionId", promotion.getId());
                map.put("promotionName", promotion.getPromotionName());
                map.put("full", promotion.getFull());
                map.put("minus", promotion.getMinus());
                map.put("startTime", promotion.getStartTime().toString());
                map.put("endTime", promotion.getEndTime().toString());
                return map;
            }).collect(Collectors.toList());
            return ApiResponse.success(promotionList);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @GetMapping("/detail")
    public ApiResponse<?> getPromotionDetail(@RequestParam Integer promotionId) {
        try {
            Promotion promotion = promotionService.getPromotionById(promotionId);
            Map<String, Object> promotionDetail = Map.of(
                "promotionName", promotion.getPromotionName(),
                "full", promotion.getFull(),
                "minus", promotion.getMinus(),
                "startTime", promotion.getStartTime().toString(),
                "endTime", promotion.getEndTime().toString()
            );
            return ApiResponse.success(promotionDetail);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "促销活动不存在");
        }
    }

    @DeleteMapping
    public ApiResponse<?> deletePromotion(@RequestBody Map<String, Integer> requestBody) {
        try {
            Integer promotionId = requestBody.get("promotionId");
            promotionService.deletePromotion(promotionId);
            return ApiResponse.success(Map.of("success", true));
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "删除失败");
        }
    }
}
