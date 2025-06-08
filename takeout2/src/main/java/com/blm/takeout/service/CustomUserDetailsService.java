package com.blm.takeout.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.blm.takeout.entity.User;
import com.blm.takeout.repository.UserRepository;
import com.blm.takeout.entity.User.Role;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String[] parts = username.split(":");
        if (parts.length != 2) {
            throw new UsernameNotFoundException("用户名格式错误");
        }
        String phonenumber = parts[0];
        Role role = Role.valueOf(parts[1]);
        User user = userRepository.findByPhonenumberAndRole(phonenumber, role)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));

        return user;
    }
}