package com.blm.takeout.controller;

import com.blm.takeout.dto.UserDTO;
import com.blm.takeout.entity.User;
import com.blm.takeout.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
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
} 