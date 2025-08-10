package com.example.campusapp.controller;

import com.example.campusapp.model.Country;
import com.example.campusapp.repo.CountryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
public class CountryController {

    private final CountryRepository countryRepository;

    public CountryController(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','USER')")
    public ResponseEntity<List<Country>> list() {
        return ResponseEntity.ok(countryRepository.findAll());
    }
}