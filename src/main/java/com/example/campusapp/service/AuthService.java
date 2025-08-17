package com.example.campusapp.service;

import com.example.campusapp.dto.AuthDtos;
import com.example.campusapp.model.AppUser;
import com.example.campusapp.model.Building;
import com.example.campusapp.model.Campus;
import com.example.campusapp.repo.AppUserRepository;
import com.example.campusapp.repo.BuildingRepository;
import com.example.campusapp.repo.CampusRepository;
import com.example.campusapp.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CampusRepository campusRepository;
    private final BuildingRepository buildingRepository;

    public AuthService(AuthenticationManager authenticationManager, AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, JwtService jwtService, CampusRepository campusRepository, BuildingRepository buildingRepository) {
        this.authenticationManager = authenticationManager;
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.campusRepository = campusRepository;
        this.buildingRepository = buildingRepository;
    }

    @Transactional(readOnly = true)
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

        List<AuthDtos.CampusProfile> campusProfiles = new ArrayList<>();
        List<Campus> accessibleCampuses;
        boolean isSuperAdmin = user.getRole() == com.example.campusapp.model.Role.SUPER_ADMIN;
        if (isSuperAdmin) {
            accessibleCampuses = campusRepository.findAll();
        } else {
            accessibleCampuses = user.getCampuses() == null ? List.of() : new ArrayList<>(user.getCampuses());
        }
        for (Campus campus : accessibleCampuses) {
            var city = campus.getCity();
            var country = city != null ? city.getCountry() : null;
            List<Building> buildings = buildingRepository.findByCampusId(campus.getId());
            List<AuthDtos.BuildingInfo> buildingInfos = buildings.stream()
                    .map(b -> new AuthDtos.BuildingInfo(b.getId(), b.getName()))
                    .toList();
            AuthDtos.CampusProfile cp = new AuthDtos.CampusProfile(
                    campus.getId(),
                    campus.getName(),
                    city != null ? city.getId() : null,
                    city != null ? city.getName() : null,
                    country != null ? country.getId() : null,
                    country != null ? country.getName() : null,
                    buildingInfos
            );
            campusProfiles.add(cp);
        }
        AuthDtos.Profile profile = new AuthDtos.Profile(campusProfiles);
        return new AuthDtos.AuthResponse(token, user.getRole().name(), campusId, user.isMustChangePassword(), profile);
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