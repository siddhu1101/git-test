package com.example.campusapp.service;

import com.example.campusapp.dto.AuthDtos;
import com.example.campusapp.model.AppUser;
import com.example.campusapp.model.Role;
import com.example.campusapp.repo.AppUserRepository;
import com.example.campusapp.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid credentials");
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        AppUser user = appUserRepository.findByUsername(request.username()).orElseThrow();
        String token = jwtService.generateToken(user, user.isMustChangePassword());
        Long campusId = null; // kept for backward compatibility in response; use claims.campusIds instead
        return new AuthDtos.AuthResponse(token, user.getRole().name(), campusId, user.isMustChangePassword());
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        AppUser user = appUserRepository.findByUsername(username).orElseThrow();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadCredentialsException("Old password incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setMustChangePassword(false);
        appUserRepository.save(user);
    }
}