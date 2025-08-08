package com.example.campusapp.controller;

import com.example.campusapp.model.Campus;
import com.example.campusapp.model.MetricData;
import com.example.campusapp.repo.CampusRepository;
import com.example.campusapp.repo.MetricDataRepository;
import com.example.campusapp.service.AccessControlService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metrics")
public class MetricController {

    private final MetricDataRepository metricDataRepository;
    private final CampusRepository campusRepository;
    private final AccessControlService accessControlService;

    public MetricController(MetricDataRepository metricDataRepository, CampusRepository campusRepository, AccessControlService accessControlService) {
        this.metricDataRepository = metricDataRepository;
        this.campusRepository = campusRepository;
        this.accessControlService = accessControlService;
    }

    @GetMapping("/campus/{campusId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','USER')")
    public ResponseEntity<List<MetricData>> getMetrics(@PathVariable Long campusId) {
        if (!accessControlService.canAccessCampus(campusId)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(metricDataRepository.findByCampusId(campusId));
    }

    @PostMapping("/campus/{campusId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<MetricData> addMetric(@PathVariable Long campusId, @RequestBody MetricData payload) {
        if (!accessControlService.canAccessCampus(campusId)) {
            return ResponseEntity.status(403).build();
        }
        Campus campus = campusRepository.findById(campusId).orElseThrow();
        MetricData md = new MetricData();
        md.setCampus(campus);
        md.setMetricKey(payload.getMetricKey());
        md.setMetricValue(payload.getMetricValue());
        return ResponseEntity.ok(metricDataRepository.save(md));
    }
}