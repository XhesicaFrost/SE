package com.blm.takeout.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blm.takeout.common.ApiResponse;
import com.blm.takeout.service.SellerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sellerHome")
public class SellerhomeController {
    private final SellerService sellerService;

    @GetMapping
    public ApiResponse<?> getSellerStats(@RequestParam Integer sellerId) {
        try {
            Map<String, Object> stats = sellerService.getTodayStats(sellerId);
            return ApiResponse.success(stats);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "获取商家统计信息失败: " + e.getMessage());
        }
    }
}
