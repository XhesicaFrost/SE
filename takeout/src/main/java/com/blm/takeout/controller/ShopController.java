package com.blm.takeout.controller;

import com.blm.takeout.entity.Shop;
import com.blm.takeout.service.ShopService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/shops")
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Shop>> searchShops(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Shop> shops = shopService.searchShops(keyword, pageRequest);
        return ResponseEntity.ok(shops);
    }

    @GetMapping("/{shopId}")
    public Map<String, Object> getShopDetails(@PathVariable Integer shopId) {
        try {
            return Map.of(
                "code", 200,
                "success", true,
                "data", shopService.getShopDetails(shopId)
            );
        } catch (Exception e) {
            return Map.of(
                "code", 500,
                "success", false,
                "message", "获取店铺详情失败：" + e.getMessage()
            );
        }
    }

    @GetMapping("/{shopId}/hot-items")
    public Map<String, Object> getShopHotItems(
            @PathVariable Integer shopId,
            @RequestParam(required = false) Integer limit) {
        try {
            return Map.of(
                "code", 200,
                "success", true,
                "data", shopService.getShopHotItems(shopId, limit)
            );
        } catch (Exception e) {
            return Map.of(
                "code", 500,
                "success", false,
                "message", "获取店铺热销商品失败：" + e.getMessage()
            );
        }
    }

    @GetMapping("/{shopId}/categories")
    public Map<String, Object> getShopCategories(@PathVariable Integer shopId) {
        try {
            return Map.of(
                "code", 200,
                "success", true,
                "data", shopService.getShopCategories(shopId)
            );
        } catch (Exception e) {
            return Map.of(
                "code", 500,
                "success", false,
                "message", "获取店铺商品分类失败：" + e.getMessage()
            );
        }
    }
} 