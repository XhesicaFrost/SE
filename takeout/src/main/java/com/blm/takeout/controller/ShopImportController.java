package com.blm.takeout.controller;

import com.blm.takeout.service.impl.ShopImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class ShopImportController {

    private final ShopImportService shopImportService;

    @Autowired
    public ShopImportController(ShopImportService shopImportService) {
        this.shopImportService = shopImportService;
    }

    @PostMapping("/import-shops")
    public ResponseEntity<Map<String, Object>> importShops() {
        try {
            shopImportService.importSellersToShops();
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("message", "商家数据导入成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "商家数据导入失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
} 