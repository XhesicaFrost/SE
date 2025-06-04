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
        if (userRepository.existsByPhonenumberAndRole(registerDto.getPhonenumber(), registerDto.getRole())) {
            throw new BusinessException("该手机号已注册该角色");
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
        String username = loginDto.getPhonenumber() + ":" + loginDto.getRole();
        //System.out.println("登录用户名：" + username);
        //System.out.println("用户输入的密码：" + loginDto.getPassword());
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                username, 
                loginDto.getPassword()
            )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();
        //System.out.println("数据库中的加密密码：" + user.getPassword());
        String token = jwtUtils.generateToken(user);
        //System.out.println(token);
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