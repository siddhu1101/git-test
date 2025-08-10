package com.example.campusapp.repo;

import com.example.campusapp.model.Building;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface BuildingRepository extends JpaRepository<Building, Long> {
    List<Building> findByCampusId(Long campusId);
    List<Building> findByCampusIdIn(Collection<Long> campusIds);
}