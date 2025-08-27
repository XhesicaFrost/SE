package com.blm.takeout.dto;

import lombok.Data;
import jakarta.validation.constraints.*; // 修改这里
import com.blm.takeout.entity.User.Role;

@Data
public class UserRegisterDto {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度必须在4-20之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20之间")
    private String password;

    @NotBlank(message = "手机号码不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    private String phonenumber;

    @Email(message = "邮箱格式不正确")
    private String email;

    @NotNull(message = "身份不能为空")
    private Role role;
    
    private String avatarurl;
}