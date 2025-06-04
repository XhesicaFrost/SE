package com.blm.takeout.controller;

import com.blm.takeout.service.ShopDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
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

    @GetMapping("/{shopId}/hot-items")
    public ResponseEntity<Map<String, Object>> getHotItems(
            @PathVariable Integer shopId,
            @RequestParam(required = false) Integer limit) {
        try {
            List<Map<String, Object>> hotItems = shopDetailService.getHotItems(shopId, limit);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("data", hotItems);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "获取热销商品失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/{shopId}/categories")
    public ResponseEntity<Map<String, Object>> getCategories(@PathVariable Integer shopId) {
        try {
            List<Map<String, Object>> categories = shopDetailService.getCategories(shopId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("data", categories);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "获取商品分类失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
} 