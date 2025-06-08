package com.blm.takeout.controller;

import com.blm.takeout.entity.ShopType;
import com.blm.takeout.service.ShopTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shop/types")
public class ShopTypeController {
    private final ShopTypeService shopTypeService;

    @Autowired
    public ShopTypeController(ShopTypeService shopTypeService) {
        this.shopTypeService = shopTypeService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllShopTypes() {
        try {
            List<ShopType> shopTypes = shopTypeService.getAllShopTypes();
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("data", shopTypes);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "获取店铺类型失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
} 