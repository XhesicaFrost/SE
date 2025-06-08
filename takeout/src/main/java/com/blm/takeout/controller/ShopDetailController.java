package com.blm.takeout.controller;

import com.blm.takeout.dto.ShopDTO;
import com.blm.takeout.service.ShopService;
import com.blm.takeout.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/shop")
public class ShopDetailController {

    private final ShopService shopService;

    @Autowired
    public ShopDetailController(ShopService shopService) {
        this.shopService = shopService;
    }

    @GetMapping
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
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "获取店铺详情失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
} 