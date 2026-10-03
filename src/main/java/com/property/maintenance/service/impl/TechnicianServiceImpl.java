package com.property.maintenance.service.impl;

import com.property.maintenance.dao.TechnicianDao;
import com.property.maintenance.dto.TechnicianDto;
import com.property.maintenance.exception.BadRequestException;
import com.property.maintenance.exception.ResourceNotFoundException;
import com.property.maintenance.model.Technician;
import com.property.maintenance.service.TechnicianService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TechnicianServiceImpl implements TechnicianService {

    private final TechnicianDao technicianDao;

    public TechnicianServiceImpl(TechnicianDao technicianDao) {
        this.technicianDao = technicianDao;
    }

    @Override
    public Technician createTechnician(TechnicianDto dto) {
        if (technicianDao.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Technician email already exists: " + dto.getEmail());
        }

        Technician t = new Technician();
        t.setUserId(dto.getUserId());
        t.setFirstName(dto.getFirstName());
        t.setLastName(dto.getLastName());
        t.setEmail(dto.getEmail());
        t.setPhone(dto.getPhone());
        t.setSpecialization(dto.getSpecialization().toUpperCase());
        t.setIsAvailable(dto.getIsAvailable() != null ? dto.getIsAvailable() : true);
        t.setHourlyRate(dto.getHourlyRate());
        t.setStatus(dto.getStatus() != null ? dto.getStatus().toUpperCase() : "ACTIVE");

        return technicianDao.save(t);
    }

    @Override
    public List<Technician> getAllTechnicians(String specialization, Boolean isAvailable, String status) {
        return technicianDao.findAll(specialization, isAvailable, status);
    }

    @Override
    public List<Technician> getAvailableBySpecialization(String specialization) {
        return technicianDao.findAvailableBySpecialization(specialization.toUpperCase());
    }

    @Override
    public Technician getTechnicianById(Long id) {
        return technicianDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Technician", "id", id));
    }

    @Override
    public Technician getTechnicianByUserId(Long userId) {
        return technicianDao.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Technician", "userId", userId));
    }

    @Override
    public Technician updateTechnician(Long id, TechnicianDto dto) {
        Technician t = getTechnicianById(id);

        if (!t.getEmail().equalsIgnoreCase(dto.getEmail()) && technicianDao.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Email already taken: " + dto.getEmail());
        }

        t.setFirstName(dto.getFirstName());
        t.setLastName(dto.getLastName());
        t.setEmail(dto.getEmail());
        t.setPhone(dto.getPhone());
        t.setSpecialization(dto.getSpecialization().toUpperCase());
        if (dto.getIsAvailable() != null) {
            t.setIsAvailable(dto.getIsAvailable());
        }
        t.setHourlyRate(dto.getHourlyRate());
        if (dto.getStatus() != null) {
            t.setStatus(dto.getStatus().toUpperCase());
        }

        return technicianDao.save(t);
    }

    @Override
    public Technician updateAvailability(Long id, boolean isAvailable) {
        Technician t = getTechnicianById(id);
        technicianDao.updateAvailability(id, isAvailable);
        t.setIsAvailable(isAvailable);
        return t;
    }

    @Override
    public void deleteTechnician(Long id) {
        getTechnicianById(id);
        technicianDao.deleteById(id);
    }
}
