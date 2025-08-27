package com.blm.takeout.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.blm.takeout.common.ApiResponse;
import com.blm.takeout.dto.LoginResponseDto;
import com.blm.takeout.dto.UserLoginDto;
import com.blm.takeout.dto.UserRegisterDto;
import com.blm.takeout.entity.User;
import com.blm.takeout.service.AuthService;
import com.blm.takeout.exception.BusinessException;
import com.blm.takeout.util.FileUtils;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/register")
    public ApiResponse<?> register(UserRegisterDto registerDto) {
        try {
            var user = authService.register(registerDto);
            return ApiResponse.success("注册成功", 
                Map.of(
                    "id", user.getUserid(),
                    "role", user.getRole()
                ));
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @GetMapping("/login")
    public ApiResponse<?> login(@RequestParam(required = false) String username,
                                @RequestParam String phonenumber,
                                @RequestParam String password,
                                @RequestParam String role) {
        try {
            User.Role userrole;
            switch(role.toLowerCase()) {
                case "user": userrole = User.Role.user; break;
                case "rider": userrole = User.Role.rider; break;
                case "seller": userrole = User.Role.seller; break;
                case "admin": userrole = User.Role.admin; break;
                default: throw new BusinessException("无效的用户类型");
            }
            UserLoginDto loginDto = new UserLoginDto();
            loginDto.setUsername(username);
            loginDto.setPhonenumber(phonenumber);
            loginDto.setRole(userrole);
            loginDto.setPassword(password);
            LoginResponseDto response = authService.login(loginDto);
            Map<String, Object> map = new HashMap<>();
            try {
                String base64Image = FileUtils.convertImageToBase64(response.getAvatarurl());
                map.put("userImage", base64Image); 
            } catch (IOException e) {
                map.put("userImage", null);
            }
            map.put("id", response.getUserid());
            map.put("username", response.getUsername());
            map.put("phonenumber", response.getPhonenumber());
            map.put("role", response.getRole());
            map.put("token", response.getToken());
            return ApiResponse.success("登录成功", map);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "用户名或密码错误");
        }
    }
}