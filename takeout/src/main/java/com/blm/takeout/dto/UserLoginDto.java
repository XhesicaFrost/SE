package com.blm.takeout.dto;

import jakarta.validation.constraints.*; // 修改这里
import lombok.Data;

@Data
public class UserLoginDto {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}