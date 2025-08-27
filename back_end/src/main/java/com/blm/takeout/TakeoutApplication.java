package com.blm.takeout;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.blm.takeout.entity.User;
import com.blm.takeout.entity.User.Role;
import com.blm.takeout.repository.UserRepository;

@SpringBootApplication
public class TakeoutApplication {

	public static void main(String[] args) {
		SpringApplication.run(TakeoutApplication.class, args);
	}

	@Bean
	CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
		if (!userRepository.existsByPhonenumberAndRole("11111111111", Role.admin)) {
			User admin = new User();
			admin.setUsername("admin");
			admin.setPhonenumber("11111111111");
			admin.setPassword(passwordEncoder.encode("admin123"));
			admin.setRole(Role.admin);
			userRepository.save(admin);
			System.out.println("管理员账号已创建");
			}
		};
	}
}