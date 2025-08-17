package com.example.campusapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class AuthDtos {
    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
    public record AuthResponse(String accessToken, String role, Long campusId, boolean mustChangePassword, Profile profile) {}
    public record ChangePasswordRequest(@NotBlank String oldPassword, @NotBlank String newPassword) {}
    public record CreateAdminRequest(@NotBlank String username, @NotBlank String password, @NotNull Long campusId) {}
    public record CreateUserRequest(@NotBlank String username, @NotBlank String password) {}

    public record Profile(List<CampusProfile> campuses) {}
    public record CampusProfile(Long campusId, String campusName,
                                Long cityId, String cityName,
                                Long countryId, String countryName,
                                List<BuildingInfo> buildings) {}
    public record BuildingInfo(Long id, String name) {}
}