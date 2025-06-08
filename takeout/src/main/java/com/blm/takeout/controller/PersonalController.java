package com.blm.takeout.controller;

import com.blm.takeout.entity.User;
import com.blm.takeout.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/personal")
public class PersonalController {

    @Autowired
    private UserService userService;

    @PostMapping("/edit")
    public ResponseEntity<Map<String, String>> editPersonal(@RequestParam(required = false) Integer id,
                                                          @RequestParam(required = false) String name,
                                                          @RequestParam(required = false) String phone,
                                                          @RequestParam(required = false) String image) {
        try {
            userService.updateUser(id, name, phone, image);
            return ResponseEntity.ok(Map.of("status", "success"));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("status", "fail"));
        }
    }
} 