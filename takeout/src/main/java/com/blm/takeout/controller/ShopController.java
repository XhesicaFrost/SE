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
import java.util.stream.Collectors;

@RestController
public class ShopController {

    private final ShopService shopService;

    @Autowired
    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @GetMapping("/shops")
    public ResponseEntity<?> getAllShops() {
        try {
            List<ShopDTO> shops = shopService.getAllShops();
            List<Map<String, Object>> response = shops.stream()
                .map(shop -> {
                    Map<String, Object> shopMap = new HashMap<>();
                    shopMap.put("id", shop.getId());
                    shopMap.put("image", shop.getImage());
                    shopMap.put("name", shop.getName());
                    shopMap.put("rating", shop.getRating());
                    shopMap.put("tags", shop.getTags());
                    shopMap.put("avgPrice", shop.getAvgPrice());
                    shopMap.put("distance", shop.getDistance());
                    shopMap.put("deliverTime", shop.getDeliverTime());
                    shopMap.put("products", shop.getProducts());
                    return shopMap;
                })
                .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
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
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
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
    public List<ShopDTO> getRecommendedShops(@RequestParam(required = false) String userId) {
        if (userId == null || userId.equals("undefined")) {
            return shopService.getAllShops(); // 如果没有userId或userId为undefined，返回所有商店
        }
        try {
            Integer userIdInt = Integer.parseInt(userId);
            return shopService.getRecommendedShops(userIdInt);
        } catch (NumberFormatException e) {
            return shopService.getAllShops(); // 如果userId不是有效的数字，返回所有商店
        }
    }
} 