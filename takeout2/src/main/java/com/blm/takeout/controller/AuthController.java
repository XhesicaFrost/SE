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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/register")
    public ApiResponse<?> registerWithParams(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String phonenumber,
            @RequestParam User.Role role,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String avatarurl) {
        UserRegisterDto registerDto = new UserRegisterDto();
        registerDto.setUsername(username);
        registerDto.setPassword(password);
        registerDto.setPhonenumber(phonenumber);
        registerDto.setRole(role);
        registerDto.setEmail(email);
        registerDto.setAvatarurl(avatarurl);
        return register(registerDto);
    }

    @PostMapping("/register")
    public ApiResponse<?> register(@RequestBody UserRegisterDto registerDto) {
        try {
            log.info("Received registration request - DTO: {}", registerDto);
            
            if (registerDto.getUsername() == null || registerDto.getUsername().trim().isEmpty()) {
                throw new BusinessException("用户名不能为空");
            }
            if (registerDto.getPassword() == null || registerDto.getPassword().trim().isEmpty()) {
                throw new BusinessException("密码不能为空");
            }
            if (registerDto.getPhonenumber() == null || registerDto.getPhonenumber().trim().isEmpty()) {
                throw new BusinessException("手机号不能为空");
            }
            if (registerDto.getRole() == null) {
                throw new BusinessException("角色不能为空");
            }

            // 清理输入数据
            registerDto.setUsername(registerDto.getUsername().trim());
            registerDto.setPassword(registerDto.getPassword().trim());
            registerDto.setPhonenumber(registerDto.getPhonenumber().trim());
            if (registerDto.getEmail() != null) {
                registerDto.setEmail(registerDto.getEmail().trim());
            }
            if (registerDto.getAvatarurl() != null) {
                registerDto.setAvatarurl(registerDto.getAvatarurl().trim());
            }
            
            log.info("Processing registration for user with phone: {}", registerDto.getPhonenumber());
            var user = authService.register(registerDto);
            log.info("Successfully registered user with ID: {}", user.getUserid());
            
            return ApiResponse.success("注册成功", 
                Map.of(
                    "id", user.getUserid(),
                    "role", user.getRole()
                ));
        } catch (Exception e) {
            log.error("Registration failed", e);
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @GetMapping("/login")
    public ApiResponse<?> loginWithParams(
            @RequestParam String phonenumber,
            @RequestParam String password,
            @RequestParam User.Role role,
            @RequestParam(required = false) String username) {
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsername(username);
        loginDto.setPhonenumber(phonenumber);
        loginDto.setPassword(password);
        loginDto.setRole(role);
        return login(loginDto);
    }

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody UserLoginDto loginDto) {
        try {
            log.info("Received login request - DTO: {}", loginDto);
            
            if (loginDto.getPhonenumber() == null || loginDto.getPhonenumber().trim().isEmpty()) {
                throw new BusinessException("手机号不能为空");
            }
            if (loginDto.getPassword() == null || loginDto.getPassword().trim().isEmpty()) {
                throw new BusinessException("密码不能为空");
            }
            if (loginDto.getRole() == null) {
                throw new BusinessException("角色不能为空");
            }

            // 清理输入数据
            if (loginDto.getUsername() != null) {
                loginDto.setUsername(loginDto.getUsername().trim());
            }
            loginDto.setPhonenumber(loginDto.getPhonenumber().trim());
            loginDto.setPassword(loginDto.getPassword().trim());
            
            log.info("Processing login for user with phone: {}", loginDto.getPhonenumber());
            LoginResponseDto response = authService.login(loginDto);
            log.info("Successfully logged in user with ID: {}", response.getUserid());
            
            return ApiResponse.success("登录成功", 
                Map.of(
                    "id", response.getUserid(),
                    "username", response.getUsername(),
                    "phonenumber", response.getPhonenumber(),
                    "role", response.getRole(),
                    "token", response.getToken()
                ));
        } catch (Exception e) {
            log.error("Login failed", e);
            return ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "用户名或密码错误");
        }
    }
}