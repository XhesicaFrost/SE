package com.blm.takeout.config;
import org.springframework.lang.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**") // 允许所有路径
                .allowedOrigins(
                    "http://localhost:12345",
                    "http://localhost:8080",
                    "http://127.0.0.1:8083",
                    "http://10.193.160.55:8080"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的 HTTP 方法
                .allowedHeaders("*") // 允许所有请求头
                .exposedHeaders("Authorization")
                .allowCredentials(true) // 允许发送 Cookie
                .maxAge(3600); // 预检请求缓存时间（秒）
    }
}