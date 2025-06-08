package com.blm.takeout.dto;

import com.blm.takeout.entity.User;
import lombok.Data;

@Data
public class UserDTO {
    private Integer id;
    private String username;
    private String phonenumber;
    private String email;
    private String avatarurl;
    private User.Role role;
} 