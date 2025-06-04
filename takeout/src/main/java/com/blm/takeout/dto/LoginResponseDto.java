package com.blm.takeout.dto;

import com.blm.takeout.entity.User.Role;
import lombok.Data;

@Data
public class LoginResponseDto {
    private Integer id;
    private String username;
    private String phonenumber;
    private String email;
    private Role role;
    private String token;
    private String avatar;

    public LoginResponseDto(Integer userid, String username, String phonenumber, 
                          String email, Role role, String token, String avatarurl) {
        this.id = userid;
        this.username = username;
        this.phonenumber = phonenumber;
        this.email = email;
        this.role = role;
        this.token = token;
        this.avatar = avatarurl;
    }
}