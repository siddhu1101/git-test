package com.example.campusapp.controller;

import com.example.campusapp.dto.AuthDtos;
import com.example.campusapp.service.AdminUserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @PostMapping("/admins")
    public ResponseEntity<Void> createAdmin(@Valid @RequestBody AuthDtos.CreateAdminRequest request) {
        adminUserService.createAdmin(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users")
    public ResponseEntity<Void> createUser(@Valid @RequestBody AuthDtos.CreateUserRequest request) {
        adminUserService.createUser(request);
        return ResponseEntity.noContent().build();
    }
}