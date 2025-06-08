package com.blm.takeout.controller;

import com.blm.takeout.entity.User;
import com.blm.takeout.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/personal")
public class PersonalController {

    @Autowired
    private UserService userService;

    @PostMapping("/edit")
    public ResponseEntity<Map<String, String>> editPersonal(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) MultipartFile image) {
        try {
            String imageUrl = null;
            if (image != null && !image.isEmpty()) {
                // 处理图片上传
                imageUrl = userService.saveImage(image);
            }
            userService.updateUser(id, name, phone, imageUrl);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            if (imageUrl != null) {
                response.put("imageUrl", imageUrl);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "fail");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 