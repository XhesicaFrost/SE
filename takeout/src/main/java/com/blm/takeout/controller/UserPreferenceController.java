package com.blm.takeout.controller;

import com.blm.takeout.dto.UserPreferenceDTO;
import com.blm.takeout.service.UserPreferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/preferences")
public class UserPreferenceController {

    @Autowired
    private UserPreferenceService userPreferenceService;

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUserPreference(@PathVariable String userId) {
        UserPreferenceDTO preference = userPreferenceService.getUserPreference(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("success", true);
        
        Map<String, Object> data = new HashMap<>();
        data.put("preferences", preference);
        response.put("data", data);
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> updateUserPreference(
            @PathVariable String userId,
            @RequestBody UserPreferenceDTO preferenceDTO) {
        userPreferenceService.updateUserPreference(userId, preferenceDTO);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("success", true);
        response.put("message", "偏好设置更新成功");
        
        return ResponseEntity.ok(response);
    }
} 