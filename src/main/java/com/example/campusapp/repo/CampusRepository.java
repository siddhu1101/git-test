package com.example.campusapp.repo;

import com.example.campusapp.model.Campus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampusRepository extends JpaRepository<Campus, Long> {}