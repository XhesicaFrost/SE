package com.blm.takeout.service;

import com.blm.takeout.dto.UserDTO;
import com.blm.takeout.entity.User;

public interface UserService {
    UserDTO getUserById(Integer id);
    UserDTO getUserByUsername(String username);
    UserDTO updateUser(Integer id, UserDTO userDTO);
    void updatePassword(Integer id, String oldPassword, String newPassword);
    void updateAvatar(Integer id, String avatarUrl);
    User updateUser(User user);
    User createUser(User user);
    void updateUser(Integer id, String name, String phone, String image);
} 