package com.blm.takeout.controller;

import com.blm.takeout.dto.FavoriteDTO;
import com.blm.takeout.entity.Favorite.TargetType;
import com.blm.takeout.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @PostMapping("/user/{userId}/{targetType}/{targetId}")
    public ResponseEntity<Map<String, Object>> addFavorite(
            @PathVariable Integer userId,
            @PathVariable TargetType targetType,
            @PathVariable Integer targetId,
            @RequestBody FavoriteDTO favoriteDTO) {
        favoriteService.addFavorite(userId, targetType, targetId, favoriteDTO);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("success", true);
        response.put("message", "收藏成功");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserFavorites(
            @PathVariable Integer userId,
            Pageable pageable) {
        Page<FavoriteDTO> page = favoriteService.getUserFavorites(userId, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("success", true);
        response.put("data", page);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/{targetType}")
    public ResponseEntity<Map<String, Object>> getUserFavoritesByType(
            @PathVariable Integer userId,
            @PathVariable TargetType targetType,
            Pageable pageable) {
        Page<FavoriteDTO> page = favoriteService.getUserFavoritesByType(userId, targetType, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("success", true);
        response.put("data", page);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/user/{userId}/{targetType}/{targetId}")
    public ResponseEntity<Map<String, Object>> removeFavorite(
            @PathVariable Integer userId,
            @PathVariable TargetType targetType,
            @PathVariable Integer targetId) {
        try {
            favoriteService.removeFavorite(userId, targetType, targetId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("message", "取消收藏成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
} 