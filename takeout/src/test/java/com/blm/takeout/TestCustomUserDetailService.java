package com.blm.takeout;

import com.blm.takeout.entity.User;
import com.blm.takeout.entity.User.Role;
import com.blm.takeout.repository.UserRepository;
import com.blm.takeout.service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestCustomUserDetailService {

    private UserRepository userRepository;
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        customUserDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsername_success() {
        User user = new User();
        user.setPhonenumber("13800000000");
        user.setRole(Role.user);
        when(userRepository.findByPhonenumberAndRole("13800000000", Role.user))
                .thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("13800000000:user");
        assertNotNull(result);
        assertEquals("13800000000", ((User) result).getPhonenumber());
        assertEquals(Role.user, ((User) result).getRole());
    }

    @Test
    void loadUserByUsername_formatError() {
        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("13800000000"));
        assertEquals("用户名格式错误", ex.getMessage());
    }

    @Test
    void loadUserByUsername_userNotFound() {
        when(userRepository.findByPhonenumberAndRole("13800000000", Role.user))
                .thenReturn(Optional.empty());
        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("13800000000:user"));
        assertEquals("用户不存在", ex.getMessage());
    }
}
