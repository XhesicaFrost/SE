package com.blm.takeout.controller;

import com.blm.takeout.dto.UserDTO;
import com.blm.takeout.entity.User;
import com.blm.takeout.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/personal")
public class PersonalController {
    private static final Logger logger = LoggerFactory.getLogger(PersonalController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/edit")
    public ResponseEntity<Map<String, String>> editPersonal(
            @RequestParam(required = true) Integer id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) MultipartFile avatar) {
        try {
            logger.info("开始处理用户信息更新请求 - 用户ID: {}", id);
            logger.info("接收到的参数 - 姓名: {}, 手机号: {}, 头像文件: {}", 
                name, phone, avatar != null ? avatar.getOriginalFilename() : "无");

            // 获取当前用户
            UserDTO userDTO = userService.getUserById(id);
            if (userDTO == null) {
                logger.error("用户不存在 - ID: {}", id);
                throw new RuntimeException("用户不存在");
            }
            logger.info("成功获取用户信息 - 用户名: {}", userDTO.getUsername());

            // 更新用户名
            if (name != null && !name.isEmpty()) {
                logger.info("更新用户名: {} -> {}", userDTO.getUsername(), name);
                userDTO.setUsername(name);
            }

            // 更新手机号
            if (phone != null && !phone.isEmpty()) {
                logger.info("更新手机号: {} -> {}", userDTO.getPhonenumber(), phone);
                userDTO.setPhonenumber(phone);
            }

            // 处理头像上传
            if (avatar != null && !avatar.isEmpty()) {
                logger.info("开始处理头像上传 - 文件名: {}, 大小: {} bytes", 
                    avatar.getOriginalFilename(), avatar.getSize());
                String imagePath = userService.saveImage(avatar);
                if (imagePath != null) {
                    logger.info("头像上传成功 - 保存路径: {}", imagePath);
                    userDTO.setAvatarurl(imagePath);
                } else {
                    logger.warn("头像上传失败 - 返回路径为空");
                }
            }

            // 保存更新后的用户信息
            logger.info("开始保存更新后的用户信息");
            userService.updateUser(id, userDTO);
            logger.info("用户信息更新成功");
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("用户信息更新失败", e);
            Map<String, String> response = new HashMap<>();
            response.put("status", "fail");
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
} 