package com.example.campusapp.controller;

import com.example.campusapp.model.City;
import com.example.campusapp.repo.CityRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    private final CityRepository cityRepository;

    public CityController(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','USER')")
    public ResponseEntity<List<City>> list() {
        return ResponseEntity.ok(cityRepository.findAll());
    }
}