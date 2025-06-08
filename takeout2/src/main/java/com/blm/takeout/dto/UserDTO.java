package com.blm.takeout.dto;

import com.blm.takeout.entity.User.Role;
import lombok.Data;

@Data
public class UserDTO {
    private Integer id;
    private String username;
    private String phone;
    private String email;
    private String avatar;
    private Role role;
} 