package com.blm.takeout;

import com.blm.takeout.dto.LoginResponseDto;
import com.blm.takeout.dto.UserLoginDto;
import com.blm.takeout.dto.UserRegisterDto;
import com.blm.takeout.entity.User;
import com.blm.takeout.exception.BusinessException;
import com.blm.takeout.repository.RiderRepository;
import com.blm.takeout.repository.UserRepository;
import com.blm.takeout.security.JwtUtils;
import com.blm.takeout.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestAuthService {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RiderRepository riderRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtils jwtUtils;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        userRepository = mock(UserRepository.class);
        riderRepository = mock(RiderRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtUtils = mock(JwtUtils.class);
        authService = new AuthService(authenticationManager, userRepository, riderRepository, passwordEncoder, jwtUtils);
    }

    @Test
    void register_success() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setUsername("张三");
        dto.setPassword("123456");
        dto.setPhonenumber("13800000000");
        dto.setEmail("test@test.com");
        dto.setRole(User.Role.user);
        dto.setAvatarurl("avatar.png");

        when(userRepository.existsByPhonenumberAndRole(dto.getPhonenumber(), dto.getRole())).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("encodedPwd");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = authService.register(dto);
        assertEquals("张三", user.getUsername());
        assertEquals("encodedPwd", user.getPassword());
        assertEquals("13800000000", user.getPhonenumber());
    }

    @Test
    void register_fail_alreadyExists() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setPhonenumber("13800000000");
        dto.setRole(User.Role.user);

        when(userRepository.existsByPhonenumberAndRole(dto.getPhonenumber(), dto.getRole())).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.register(dto));
        assertEquals("该手机号已注册该角色", ex.getMessage());
    }

    @Test
    void login_success() {
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setPhonenumber("13800000000");
        loginDto.setRole(User.Role.user);
        loginDto.setPassword("123456");

        User user = new User();
        user.setUserid(1);
        user.setUsername("张三");
        user.setPhonenumber("13800000000");
        user.setEmail("test@test.com");
        user.setRole(User.Role.user);
        user.setPassword("encodedPwd");
        user.setAvatarurl("avatar.png");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtUtils.generateToken(user)).thenReturn("mocked.jwt.token");

        LoginResponseDto resp = authService.login(loginDto);
        assertEquals(1, resp.getUserid());
        assertEquals("张三", resp.getUsername());
        assertEquals("mocked.jwt.token", resp.getToken());
    }
    @Test
    void login_fail_authentication() {
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setPhonenumber("13800000000");
        loginDto.setRole(User.Role.user);
        loginDto.setPassword("wrong");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("认证失败"));

        assertThrows(RuntimeException.class, () -> authService.login(loginDto));
    }
}
