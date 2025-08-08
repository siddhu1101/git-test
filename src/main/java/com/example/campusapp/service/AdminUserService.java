package com.example.campusapp.service;

import com.example.campusapp.dto.AuthDtos;
import com.example.campusapp.model.AppUser;
import com.example.campusapp.model.Campus;
import com.example.campusapp.model.Role;
import com.example.campusapp.repo.AppUserRepository;
import com.example.campusapp.repo.CampusRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminUserService {
    private final AppUserRepository appUserRepository;
    private final CampusRepository campusRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserService(AppUserRepository appUserRepository, CampusRepository campusRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.campusRepository = campusRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public void createAdmin(AuthDtos.CreateAdminRequest request) {
        if (appUserRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already exists");
        }
        Campus campus = campusRepository.findById(request.campusId())
                .orElseThrow(() -> new IllegalArgumentException("Campus not found"));
        AppUser user = new AppUser();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.ADMIN);
        user.setCampus(campus);
        user.setMustChangePassword(true);
        appUserRepository.save(user);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public void createUser(AuthDtos.CreateUserRequest request) {
        if (appUserRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already exists");
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isSuperAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));
        Campus campus;
        if (isSuperAdmin) {
            throw new IllegalArgumentException("Super admin must use createAdmin with campusId or provide a specialized endpoint");
        } else {
            // Admin creates user within own campus
            AppUser admin = appUserRepository.findByUsername(auth.getName()).orElseThrow();
            campus = admin.getCampus();
        }
        AppUser user = new AppUser();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        user.setCampus(campus);
        user.setMustChangePassword(true);
        appUserRepository.save(user);
    }
}