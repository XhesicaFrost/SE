package com.blm.takeout.dto;

import com.blm.takeout.entity.User.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserLoginDto {
    private String username;
    
    @NotBlank(message = "手机号不能为空")
    private String phonenumber;

    @NotNull(message = "角色不能为空")
    private Role role;
    
    @NotBlank(message = "密码不能为空")
    private String password;
}