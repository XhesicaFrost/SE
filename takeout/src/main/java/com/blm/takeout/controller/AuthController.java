package com.blm.takeout.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.blm.takeout.common.ApiResponse;
import com.blm.takeout.dto.LoginResponseDto;
import com.blm.takeout.dto.UserLoginDto;
import com.blm.takeout.dto.UserRegisterDto;
import com.blm.takeout.service.AuthService;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/register")
    public ApiResponse<?> register(@RequestBody UserRegisterDto registerDto) {
        try {
            var user = authService.register(registerDto);
            return ApiResponse.success("注册成功", 
                Map.of(
                    "id", user.getId(),
                    "role", user.getRole()
                ));
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @GetMapping("/login")
    public ApiResponse<?> login(@RequestBody UserLoginDto loginDto) {
        try {
            LoginResponseDto response = authService.login(loginDto);
            return ApiResponse.success("登录成功", 
                Map.of(
                    "id", response.getId(),
                    "username", response.getUsername(),
                    "role", response.getRole(),
                    "token", response.getToken(),
                    "avatar", response.getAvatar()
                ));
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "用户名或密码错误");
        }
    }
}