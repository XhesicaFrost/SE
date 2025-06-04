package com.blm.takeout.controller;

import com.blm.takeout.service.ShopDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/shops")
public class ShopDetailController {

    @Autowired
    private ShopDetailService shopDetailService;

    @GetMapping("/{shopId}")
    public ResponseEntity<Map<String, Object>> getShopDetail(@PathVariable Integer shopId) {
        try {
            Map<String, Object> shopDetail = shopDetailService.getShopDetail(shopId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("data", shopDetail);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "获取店铺详情失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
} 