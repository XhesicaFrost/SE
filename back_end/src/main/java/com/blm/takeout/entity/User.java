package com.blm.takeout.entity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "user")
@Data
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userid;

    @Column(nullable = false, length = 20)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 11)
    private String phonenumber;

    @Column(length = 100)
    private String email;

    @Column(updatable = false)
    private LocalDateTime createtime = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(length = 256)
    private String avatarurl;

    @Column(length = 256)
    private String image;

    @Table(uniqueConstraints = @UniqueConstraint(columnNames = {"phonenumber", "role"}))

    public enum Role {
        user,       // 顾客用户
        seller,   // 商家
        rider,      // 骑手
        admin       // 管理员
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}