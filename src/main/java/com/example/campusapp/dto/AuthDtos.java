package com.example.campusapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AuthDtos {
    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
    public record AuthResponse(String accessToken, String role, Long campusId, boolean mustChangePassword) {}
    public record ChangePasswordRequest(@NotBlank String oldPassword, @NotBlank String newPassword) {}
    public record CreateAdminRequest(@NotBlank String username, @NotBlank String password, @NotNull Long campusId) {}
    public record CreateUserRequest(@NotBlank String username, @NotBlank String password) {}
}