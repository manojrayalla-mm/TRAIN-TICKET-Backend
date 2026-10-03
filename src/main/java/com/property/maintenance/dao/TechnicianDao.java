package com.property.maintenance.dao;

import com.property.maintenance.model.Technician;
import java.util.List;
import java.util.Optional;

public interface TechnicianDao {
    Technician save(Technician technician);
    Optional<Technician> findById(Long id);
    Optional<Technician> findByUserId(Long userId);
    Optional<Technician> findByEmail(String email);
    List<Technician> findAll(String specialization, Boolean isAvailable, String status);
    List<Technician> findAvailableBySpecialization(String specialization);
    int updateAvailability(Long id, boolean isAvailable);
    int deleteById(Long id);
    boolean existsByEmail(String email);
}
