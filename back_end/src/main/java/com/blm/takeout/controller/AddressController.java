package com.blm.takeout.controller;

import com.blm.takeout.entity.Address;
import com.blm.takeout.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getAddresses(HttpServletRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 401);
                response.put("success", false);
                response.put("message", "用户未登录");
                return ResponseEntity.status(401).body(response);
            }
            
            // 从请求头中获取token
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 401);
                response.put("success", false);
                response.put("message", "无效的认证信息");
                return ResponseEntity.status(401).body(response);
            }
            
            String token = authHeader.substring(7);
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 401);
                response.put("success", false);
                response.put("message", "无效的token格式");
                return ResponseEntity.status(401).body(response);
            }
            
            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(payload);
            
            if (!node.has("userid")) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 401);
                response.put("success", false);
                response.put("message", "token中缺少userid字段");
                return ResponseEntity.status(401).body(response);
            }
            
            Integer userId;
            try {
                userId = node.get("userid").asInt();
            } catch (Exception e) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 401);
                response.put("success", false);
                response.put("message", "token中的userid格式无效");
                return ResponseEntity.status(401).body(response);
            }
            
            List<Address> addresses = addressService.getAddressesByUserId(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("data", addresses);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", "获取地址失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/edit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> editAddress(@RequestParam(required = false) String id,
                                                         @RequestParam(required = false) String name,
                                                         @RequestParam(required = false) String phone,
                                                         @RequestParam(required = false) String fullAddress,
                                                         @RequestParam(required = false) Boolean current) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 401);
                response.put("success", false);
                response.put("message", "用户未登录");
                return ResponseEntity.status(401).body(response);
            }
            
            Integer idInt = id != null ? Integer.parseInt(id) : null;
            addressService.updateAddress(idInt, name, phone, fullAddress, current);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("message", "更新成功");
            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("success", false);
            response.put("message", "无效的ID格式");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> addAddress(@RequestParam String name,
                                                        @RequestParam String phone,
                                                        @RequestParam String fullAddress,
                                                        @RequestParam(required = false) Boolean current,
                                                        HttpServletRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 401);
                response.put("success", false);
                response.put("message", "用户未登录");
                return ResponseEntity.status(401).body(response);
            }
            
            // 从请求头中获取token
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 401);
                response.put("success", false);
                response.put("message", "无效的认证信息");
                return ResponseEntity.status(401).body(response);
            }
            
            String token = authHeader.substring(7);
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 401);
                response.put("success", false);
                response.put("message", "无效的token格式");
                return ResponseEntity.status(401).body(response);
            }
            
            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(payload);
            
            if (!node.has("userid")) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 401);
                response.put("success", false);
                response.put("message", "token中缺少userid字段");
                return ResponseEntity.status(401).body(response);
            }
            
            Integer userId;
            try {
                userId = node.get("userid").asInt();
            } catch (Exception e) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 401);
                response.put("success", false);
                response.put("message", "token中的userid格式无效");
                return ResponseEntity.status(401).body(response);
            }
            
            addressService.addAddress(userId, name, phone, fullAddress, current);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("message", "添加成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @DeleteMapping("/delete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> deleteAddress(@RequestBody Map<String, String> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 401);
                response.put("success", false);
                response.put("message", "用户未登录");
                return ResponseEntity.status(401).body(response);
            }
            
            addressService.deleteAddress(Integer.parseInt(request.get("id")));
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("message", "删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/current")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> setCurrentAddress(@RequestBody Map<String, String> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 401);
                response.put("success", false);
                response.put("message", "用户未登录");
                return ResponseEntity.status(401).body(response);
            }
            
            addressService.setCurrentAddress(Integer.parseInt(request.get("id")));
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("message", "设置成功");
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