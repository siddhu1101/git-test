package com.example.campusapp.controller;

import com.example.campusapp.model.Building;
import com.example.campusapp.model.Campus;
import com.example.campusapp.repo.BuildingRepository;
import com.example.campusapp.repo.CampusRepository;
import com.example.campusapp.service.AccessControlService;
import com.example.campusapp.service.RequestContextClaimsHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buildings")
public class BuildingController {

    private final BuildingRepository buildingRepository;
    private final CampusRepository campusRepository;
    private final AccessControlService accessControlService;

    public BuildingController(BuildingRepository buildingRepository, CampusRepository campusRepository, AccessControlService accessControlService) {
        this.buildingRepository = buildingRepository;
        this.campusRepository = campusRepository;
        this.accessControlService = accessControlService;
    }

    @GetMapping("/campus/{campusId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','USER')")
    public ResponseEntity<List<Building>> listByCampus(@PathVariable Long campusId) {
        if (!accessControlService.canAccessCampus(campusId)) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(buildingRepository.findByCampusId(campusId));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','USER')")
    public ResponseEntity<List<Building>> listMyBuildings() {
        var claims = RequestContextClaimsHolder.getClaims();
        if (claims == null) return ResponseEntity.status(401).build();
        var campusIds = (List<Integer>) claims.get("campusIds", List.class);
        if (campusIds == null || campusIds.isEmpty()) return ResponseEntity.ok(List.of());
        var ids = campusIds.stream().map(Long::valueOf).toList();
        return ResponseEntity.ok(buildingRepository.findByCampusIdIn(ids));
    }

    @PostMapping("/campus/{campusId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<Building> create(@PathVariable Long campusId, @RequestBody Building payload) {
        if (!accessControlService.canAccessCampus(campusId)) return ResponseEntity.status(403).build();
        Campus campus = campusRepository.findById(campusId).orElseThrow();
        Building b = new Building();
        b.setName(payload.getName());
        b.setCampus(campus);
        return ResponseEntity.ok(buildingRepository.save(b));
    }
}