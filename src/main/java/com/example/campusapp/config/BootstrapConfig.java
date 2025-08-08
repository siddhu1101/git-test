package com.example.campusapp.config;

import com.example.campusapp.model.AppUser;
import com.example.campusapp.model.Role;
import com.example.campusapp.repo.AppUserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BootstrapConfig {

    @Bean
    ApplicationRunner seedSuperAdmin(AppUserRepository userRepo, PasswordEncoder encoder) {
        return args -> {
            userRepo.findByUsername("superadmin").orElseGet(() -> {
                AppUser sa = new AppUser();
                sa.setUsername("superadmin");
                sa.setPassword(encoder.encode("admin123"));
                sa.setRole(Role.SUPER_ADMIN);
                sa.setMustChangePassword(true);
                return userRepo.save(sa);
            });
        };
    }
}