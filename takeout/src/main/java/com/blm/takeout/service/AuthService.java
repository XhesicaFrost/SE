package com.blm.takeout.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.blm.takeout.dto.LoginResponseDto;
import com.blm.takeout.dto.UserLoginDto;
import com.blm.takeout.dto.UserRegisterDto;
import com.blm.takeout.entity.User;
import com.blm.takeout.exception.BusinessException;
import com.blm.takeout.repository.UserRepository;
import com.blm.takeout.security.JwtUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Transactional
    public User register(UserRegisterDto registerDto) {
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        if (userRepository.existsByPhonenumber(registerDto.getPhonenumber())) {
            throw new BusinessException("手机号码已注册");
        }
        
        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setPhonenumber(registerDto.getPhonenumber());
        user.setEmail(registerDto.getEmail());
        user.setRole(registerDto.getRole());
        user.setAvatarurl(registerDto.getAvatarurl());
        
        return userRepository.save(user);
    }

    public LoginResponseDto login(UserLoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(), 
                loginDto.getPassword()
            )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();
        String token = jwtUtils.generateToken(user);
        
        return new LoginResponseDto(
            user.getUserid(), 
            user.getUsername(), 
            user.getPhonenumber(), 
            user.getEmail(),
            user.getRole(), 
            token, 
            user.getAvatarurl());
    }
}