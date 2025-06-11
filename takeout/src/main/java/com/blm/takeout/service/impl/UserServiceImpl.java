package com.blm.takeout.service.impl;

import com.blm.takeout.dto.UserDTO;
import com.blm.takeout.entity.User;
import com.blm.takeout.repository.UserRepository;
import com.blm.takeout.service.UserService;
import com.blm.takeout.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

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
        logger.debug("Updating user - id: {}, name: {}, phone: {}, image: {}", id, name, phone, image);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (name != null && !name.trim().isEmpty()) {
            user.setUsername(name);
        }
        if (phone != null && !phone.trim().isEmpty()) {
            user.setPhonenumber(phone);
        }
        if (image != null && !image.trim().isEmpty()) {
            user.setAvatarurl(image);
        }
        
        try {
            userRepository.save(user);
            logger.debug("User updated successfully");
        } catch (Exception e) {
            logger.error("Error updating user: {}", e.getMessage());
            throw new RuntimeException("更新用户信息失败: " + e.getMessage());
        }
    }

    @Override
    public String saveImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            logger.warn("Received empty file");
            return null;
        }

        try {
            return FileUtils.saveImage(file);
        } catch (Exception e) {
            logger.error("Error saving image: {}", e.getMessage());
            throw new IOException("保存图片失败: " + e.getMessage());
        }
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