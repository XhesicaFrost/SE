package com.blm.takeout.controller;

import com.blm.takeout.dto.UserDTO;
import com.blm.takeout.entity.User;
import com.blm.takeout.service.UserService;
import com.blm.takeout.service.ShopService;
import com.blm.takeout.util.FileUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final ShopService shopService;

    @Autowired
    public UserController(UserService userService, ShopService shopService) {
        this.userService = userService;
        this.shopService = shopService;
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Integer id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users/username/{username}")
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.principal.username")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        UserDTO user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Integer id,
            @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/users/{id}/password")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<Void> updatePassword(
            @PathVariable Integer id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        userService.updatePassword(id, oldPassword, newPassword);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{id}/avatar")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<Void> updateAvatar(
            @PathVariable Integer id,
            @RequestParam String avatarUrl) {
        userService.updateAvatar(id, avatarUrl);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/personal/edit")
    public ResponseEntity<Map<String, String>> editPersonalInfo(@ModelAttribute UserDTO userDTO) {
        try {
            userService.updateUser(userDTO.getId(), userDTO);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "fail");
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUserInfo(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Integer userId = Integer.parseInt(token.split("\\.")[1]); // 简单解析，实际应该使用JWT工具类
            UserDTO user = userService.getUserById(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            
            List<Map<String, Object>> dataList = new ArrayList<>();
            Map<String, Object> data = new HashMap<>();
            data.put("id", user.getId());
            data.put("name", user.getUsername());
            
            // 将图片转换为base64
            if (user.getAvatarurl() != null && !user.getAvatarurl().isEmpty()) {
                try {
                    String base64Image = FileUtils.convertImageToBase64(user.getAvatarurl());
                    data.put("image", base64Image);
                } catch (IOException e) {
                    e.printStackTrace();
                    data.put("image", "");
                }
            } else {
                data.put("image", "");
            }
            
            data.put("rating", 5.0);
            data.put("tags", new ArrayList<>());
            data.put("avgPrice", null);
            data.put("distance", null);
            data.put("deliverTime", null);
            data.put("products", new ArrayList<>());
            data.put("address", "");
            data.put("sales", 0);
            data.put("status", "正常");
            data.put("userId", user.getId());
            
            dataList.add(data);
            response.put("data", dataList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping
    public ResponseEntity<?> getRecommendedShops(@RequestHeader("Authorization") String token) {
        try {
            // 从 token 中获取用户 ID
            String userIdStr = token.split("\\.")[1];
            String decodedPayload = new String(java.util.Base64.getDecoder().decode(userIdStr));
            Map<String, Object> payload = new com.fasterxml.jackson.databind.ObjectMapper().readValue(decodedPayload, Map.class);
            Integer userId = (Integer) payload.get("userid");

            // 获取推荐店铺
            var shops = shopService.getRecommendedShops(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("data", shops);
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