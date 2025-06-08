package com.blm.takeout.controller;

import com.blm.takeout.dto.ShopDTO;
import com.blm.takeout.entity.Shop;
import com.blm.takeout.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ShopController {

    private final ShopService shopService;

    @Autowired
    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @GetMapping("/shops")
    public ResponseEntity<List<ShopDTO>> getAllShops() {
        try {
            List<ShopDTO> shops = shopService.getAllShops();
            return ResponseEntity.ok(shops);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/shops/search")
    public ResponseEntity<Page<Shop>> searchShops(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Shop> shops = shopService.searchShops(keyword, pageRequest);
        return ResponseEntity.ok(shops);
    }

    @GetMapping("/shop")
    public ResponseEntity<Map<String, Object>> getShopDetail(@RequestParam Integer shopId) {
        try {
            ShopDTO shop = shopService.getShopById(shopId);
            Map<String, Object> response = new HashMap<>();
            response.put("name", shop.getName());
            response.put("image", shop.getImage());
            response.put("address", shop.getAddress());
            response.put("rating", shop.getRating());
            response.put("monthlySales", shop.getSales());
            response.put("deliveryTime", shop.getDeliverTime());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/shops/{shopId}/hot-items")
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

    @GetMapping("/shops/{shopId}/categories")
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

    @GetMapping("/user")
    public List<ShopDTO> getRecommendedShops(@RequestParam Integer userId) {
        return shopService.getRecommendedShops(userId);
    }
} 