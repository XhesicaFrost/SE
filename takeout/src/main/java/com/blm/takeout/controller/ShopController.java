package com.blm.takeout.controller;

import com.blm.takeout.dto.ShopDTO;
import com.blm.takeout.dto.ItemDTO;
import com.blm.takeout.dto.ProductImageDTO;
import com.blm.takeout.entity.Shop;
import com.blm.takeout.service.ShopService;
import com.blm.takeout.util.FileUtils;
import com.blm.takeout.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/shops")
public class ShopController {

    private final ShopService shopService;

    @Autowired
    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @GetMapping
    public ResponseEntity<?> getAllShops() {
        try {
            List<ShopDTO> shops = shopService.getRecommendedShops(1); // 暂时使用固定用户ID
            List<Map<String, Object>> shopList = new ArrayList<>();
            
            for (ShopDTO shop : shops) {
                Map<String, Object> shopMap = new HashMap<>();
                shopMap.put("id", shop.getId());
                shopMap.put("name", shop.getName());
                shopMap.put("address", shop.getAddress());
                shopMap.put("rating", shop.getRating());
                shopMap.put("sales", shop.getSales());
                shopMap.put("status", shop.getStatus());
                shopMap.put("userId", shop.getUserId());
                shopMap.put("deliverTime", shop.getDeliverTime());
                shopMap.put("avgPrice", shop.getAvgPrice());
                shopMap.put("distance", shop.getDistance());
                shopMap.put("tags", shop.getTags());
                
                // 转换店铺图片为base64
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
                
                // 转换商品图片为base64
                List<Map<String, Object>> products = new ArrayList<>();
                for (ProductImageDTO item : shop.getProducts()) {
                    Map<String, Object> productMap = new HashMap<>();
                    
                    if (item.getImage() != null && !item.getImage().isEmpty()) {
                        try {
                            String base64Image = FileUtils.convertImageToBase64(item.getImage());
                            productMap.put("image", base64Image);
                        } catch (IOException e) {
                            e.printStackTrace();
                            productMap.put("image", "");
                        }
                    } else {
                        productMap.put("image", "");
                    }
                    products.add(productMap);
                }
                shopMap.put("products", products);
                shopList.add(shopMap);
            }
            
            return ResponseEntity.ok(new ApiResponse<>(200, "获取推荐店铺成功", shopList));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(500, "获取推荐店铺失败: " + e.getMessage(), null));
        }
    }

    @GetMapping("/shops")
    public ResponseEntity<?> getAllShopsOld() {
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
} 