package com.blm.takeout.controller;

import com.blm.takeout.dto.ShopDTO;
import com.blm.takeout.entity.Shop;
import com.blm.takeout.service.ShopService;
import com.blm.takeout.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
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
                    
                    // 将图片转换为base64
                    if (shop.getImage() != null && !shop.getImage().isEmpty()) {
                        try {
                            String base64Image = FileUtils.convertImageToBase64(shop.getImage());
                            shopMap.put("image", base64Image);
                        } catch (IOException e) {
                            e.printStackTrace();
                            shopMap.put("image", "");
                        }
                    } else {
                        shopMap.put("image", "");
                    }
                    
                    shopMap.put("name", shop.getName());
                    shopMap.put("rating", shop.getRating());
                    shopMap.put("tags", shop.getTags());
                    shopMap.put("avgPrice", shop.getAvgPrice());
                    shopMap.put("distance", shop.getDistance());
                    shopMap.put("deliverTime", shop.getDeliverTime());
                    
                    // 处理商品图片
                    if (shop.getProducts() != null) {
                        List<Map<String, Object>> products = shop.getProducts().stream()
                            .map(product -> {
                                Map<String, Object> productMap = new HashMap<>();
                                if (product.getImage() != null && !product.getImage().isEmpty()) {
                                    try {
                                        String base64Image = FileUtils.convertImageToBase64(product.getImage());
                                        productMap.put("image", base64Image);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        productMap.put("image", "");
                                    }
                                } else {
                                    productMap.put("image", "");
                                }
                                return productMap;
                            })
                            .collect(Collectors.toList());
                        shopMap.put("products", products);
                    }
                    
                    shopMap.put("address", shop.getAddress());
                    shopMap.put("sales", shop.getSales());
                    shopMap.put("status", shop.getStatus());
                    shopMap.put("userId", shop.getUserId());
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
            response.put("code", 200);
            response.put("success", true);
            
            Map<String, Object> data = new HashMap<>();
            data.put("id", shop.getId());
            data.put("name", shop.getName());
            
            // 将图片转换为base64
            if (shop.getImage() != null && !shop.getImage().isEmpty()) {
                try {
                    String base64Image = FileUtils.convertImageToBase64(shop.getImage());
                    data.put("image", base64Image);
                } catch (IOException e) {
                    e.printStackTrace();
                    data.put("image", "");
                }
            } else {
                data.put("image", "");
            }
            
            data.put("address", shop.getAddress());
            data.put("rating", shop.getRating());
            data.put("monthlySales", shop.getSales());
            data.put("deliveryTime", shop.getDeliverTime());
            
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
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

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getRecommendedShops(HttpServletRequest request) {
        try {
            // 从请求头中获取token
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 401);
                response.put("success", false);
                response.put("message", "无效的认证信息");
                return ResponseEntity.status(401).body(response);
            }
            
            String token = authHeader.substring(7); // 移除"Bearer "前缀
            String[] parts = token.split("\\.");
            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> claims = mapper.readValue(payload, Map.class);
            
            // 打印claims内容以便调试
            System.out.println("JWT Claims: " + claims);
            
            // 尝试不同的字段名
            Object userIdObj = claims.get("userId");
            if (userIdObj == null) {
                userIdObj = claims.get("userid");
            }
            if (userIdObj == null) {
                userIdObj = claims.get("user_id");
            }
            
            if (userIdObj == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 401);
                response.put("success", false);
                response.put("message", "无效的用户信息");
                return ResponseEntity.status(401).body(response);
            }
            
            Integer userId;
            if (userIdObj instanceof Integer) {
                userId = (Integer) userIdObj;
            } else if (userIdObj instanceof String) {
                userId = Integer.parseInt((String) userIdObj);
            } else {
                userId = Integer.parseInt(userIdObj.toString());
            }
            
            List<ShopDTO> shops = shopService.getRecommendedShops(userId);
            List<Map<String, Object>> shopList = shops.stream()
                .map(shop -> {
                    Map<String, Object> shopMap = new HashMap<>();
                    shopMap.put("id", shop.getId());
                    
                    // 将店铺图片转换为base64
                    if (shop.getImage() != null && !shop.getImage().isEmpty()) {
                        try {
                            String base64Image = FileUtils.convertImageToBase64(shop.getImage());
                            shopMap.put("image", base64Image);
                        } catch (IOException e) {
                            e.printStackTrace();
                            shopMap.put("image", "");
                        }
                    } else {
                        shopMap.put("image", "");
                    }
                    
                    shopMap.put("name", shop.getName());
                    shopMap.put("rating", shop.getRating());
                    shopMap.put("tags", shop.getTags());
                    shopMap.put("avgPrice", shop.getAvgPrice());
                    shopMap.put("distance", shop.getDistance());
                    shopMap.put("deliverTime", shop.getDeliverTime());
                    
                    // 处理商品图片
                    if (shop.getProducts() != null) {
                        List<Map<String, Object>> products = shop.getProducts().stream()
                            .map(product -> {
                                Map<String, Object> productMap = new HashMap<>();
                                if (product.getImage() != null && !product.getImage().isEmpty()) {
                                    try {
                                        String base64Image = FileUtils.convertImageToBase64(product.getImage());
                                        productMap.put("image", base64Image);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        productMap.put("image", "");
                                    }
                                } else {
                                    productMap.put("image", "");
                                }
                                return productMap;
                            })
                            .collect(Collectors.toList());
                        shopMap.put("products", products);
                    }
                    
                    shopMap.put("address", shop.getAddress());
                    shopMap.put("sales", shop.getSales());
                    shopMap.put("status", shop.getStatus());
                    shopMap.put("userId", shop.getUserId());
                    return shopMap;
                })
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("data", shopList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "获取推荐店铺失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
} 