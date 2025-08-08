package com.example.campusapp.controller;

import com.example.campusapp.model.Campus;
import com.example.campusapp.repo.CampusRepository;
import com.example.campusapp.service.AccessControlService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/campuses")
public class CampusController {

    private final CampusRepository campusRepository;
    private final AccessControlService accessControlService;

    public CampusController(CampusRepository campusRepository, AccessControlService accessControlService) {
        this.campusRepository = campusRepository;
        this.accessControlService = accessControlService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','USER')")
    public ResponseEntity<List<Campus>> list() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        boolean isSuperAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));
        if (isSuperAdmin) {
            return ResponseEntity.ok(campusRepository.findAll());
        }
        var claims = com.example.campusapp.service.RequestContextClaimsHolder.getClaims();
        java.util.List<Integer> campusIds = claims != null ? claims.get("campusIds", java.util.List.class) : java.util.List.of();
        if (campusIds == null || campusIds.isEmpty()) return ResponseEntity.ok(java.util.List.of());
        var ids = campusIds.stream().map(Long::valueOf).toList();
        return ResponseEntity.ok(campusRepository.findAllById(ids));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','USER')")
    public ResponseEntity<Campus> get(@PathVariable Long id) {
        if (!accessControlService.canAccessCampus(id)) return ResponseEntity.status(403).build();
        return campusRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}