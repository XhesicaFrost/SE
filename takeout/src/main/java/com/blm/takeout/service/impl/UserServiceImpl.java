package com.blm.takeout.service.impl;

import com.blm.takeout.dto.UserDTO;
import com.blm.takeout.entity.User;
import com.blm.takeout.repository.UserRepository;
import com.blm.takeout.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDTO getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return convertToDTO(user);
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return convertToDTO(user);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Integer id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (userDTO.getUsername() != null) {
            user.setUsername(userDTO.getUsername());
        }
        if (userDTO.getPhonenumber() != null) {
            user.setPhonenumber(userDTO.getPhonenumber());
        }
        if (userDTO.getAvatarurl() != null) {
            user.setAvatarurl(userDTO.getAvatarurl());
        }
        
        return convertToDTO(userRepository.save(user));
    }

    @Override
    @Transactional
    public void updatePassword(Integer id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateAvatar(Integer id, String avatarUrl) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setAvatarurl(avatarUrl);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getUserid())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (user.getUsername() != null) {
            existingUser.setUsername(user.getUsername());
        }
        if (user.getPhonenumber() != null) {
            existingUser.setPhonenumber(user.getPhonenumber());
        }
        if (user.getAvatarurl() != null) {
            existingUser.setAvatarurl(user.getAvatarurl());
        }
        
        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUser(Integer id, String name, String phone, String image) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (name != null) user.setUsername(name);
        if (phone != null) user.setPhonenumber(phone);
        if (image != null) user.setAvatarurl(image);
        
        userRepository.save(user);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getUserid());
        dto.setUsername(user.getUsername());
        dto.setPhonenumber(user.getPhonenumber());
        dto.setEmail(user.getEmail());
        dto.setAvatarurl(user.getAvatarurl());
        dto.setRole(user.getRole());
        return dto;
    }
} 