package com.blm.takeout.controller;

import com.blm.takeout.entity.UserLocation;
import com.blm.takeout.service.UserLocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserLocationController {
    private final UserLocationService userLocationService;

    public UserLocationController(UserLocationService userLocationService) {
        this.userLocationService = userLocationService;
    }

    @GetMapping("/location")
    public ResponseEntity<Map<String, Object>> getUserLocation(@RequestParam Integer userId) {
        try {
            return userLocationService.getUserLocation(userId)
                .map(location -> {
                    Map<String, Object> locationData = new HashMap<>();
                    locationData.put("latitude", location.getLatitude());
                    locationData.put("longitude", location.getLongitude());
                    locationData.put("address", location.getAddress());

                    Map<String, Object> response = new HashMap<>();
                    response.put("code", 200);
                    response.put("success", true);
                    response.put("data", locationData);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("code", 404);
                    response.put("success", false);
                    response.put("message", "未找到用户位置信息");
                    return ResponseEntity.ok(response);
                });
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "获取位置失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/location")
    public ResponseEntity<Map<String, Object>> updateUserLocation(
            @RequestParam Integer userId,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false) String address) {
        try {
            UserLocation location = userLocationService.updateUserLocation(userId, latitude, longitude, address);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("message", "位置更新成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "更新位置失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
} 