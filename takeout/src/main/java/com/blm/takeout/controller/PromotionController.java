package com.blm.takeout.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blm.takeout.common.ApiResponse;
import com.blm.takeout.service.PromotionService;

import lombok.RequiredArgsConstructor;

import java.util.Map;

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
}
