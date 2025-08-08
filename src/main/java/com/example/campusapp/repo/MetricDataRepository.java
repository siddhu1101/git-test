package com.example.campusapp.repo;

import com.example.campusapp.model.MetricData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MetricDataRepository extends JpaRepository<MetricData, Long> {
    List<MetricData> findByCampusId(Long campusId);
}