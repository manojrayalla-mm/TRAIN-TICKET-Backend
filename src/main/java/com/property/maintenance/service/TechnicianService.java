package com.property.maintenance.service;

import com.property.maintenance.dto.TechnicianDto;
import com.property.maintenance.model.Technician;
import java.util.List;

public interface TechnicianService {
    Technician createTechnician(TechnicianDto dto);
    List<Technician> getAllTechnicians(String specialization, Boolean isAvailable, String status);
    List<Technician> getAvailableBySpecialization(String specialization);
    Technician getTechnicianById(Long id);
    Technician getTechnicianByUserId(Long userId);
    Technician updateTechnician(Long id, TechnicianDto dto);
    Technician updateAvailability(Long id, boolean isAvailable);
    void deleteTechnician(Long id);
}
