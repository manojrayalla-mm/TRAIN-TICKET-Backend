package com.property.maintenance.controller;

import com.property.maintenance.dto.ApiResponse;
import com.property.maintenance.dto.TechnicianDto;
import com.property.maintenance.model.Technician;
import com.property.maintenance.service.TechnicianService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/technicians")
public class TechnicianController {

    private final TechnicianService technicianService;

    public TechnicianController(TechnicianService technicianService) {
        this.technicianService = technicianService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Technician>> createTechnician(@RequestBody TechnicianDto dto) {
        Technician technician = technicianService.createTechnician(dto);
        return new ResponseEntity<>(ApiResponse.success("Technician registered successfully", technician), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Technician>>> getAllTechnicians(
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) Boolean isAvailable,
            @RequestParam(required = false) String status) {
        List<Technician> technicians = technicianService.getAllTechnicians(specialization, isAvailable, status);
        return ResponseEntity.ok(ApiResponse.success("Technicians fetched successfully", technicians));
    }

    @GetMapping("/available/{specialization}")
    public ResponseEntity<ApiResponse<List<Technician>>> getAvailableBySpecialization(@PathVariable String specialization) {
        List<Technician> available = technicianService.getAvailableBySpecialization(specialization);
        return ResponseEntity.ok(ApiResponse.success("Available technicians retrieved", available));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Technician>> getTechnicianById(@PathVariable Long id) {
        Technician technician = technicianService.getTechnicianById(id);
        return ResponseEntity.ok(ApiResponse.success("Technician fetched successfully", technician));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Technician>> updateTechnician(
            @PathVariable Long id,
            @RequestBody TechnicianDto dto) {
        Technician technician = technicianService.updateTechnician(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Technician updated successfully", technician));
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<ApiResponse<Technician>> updateAvailability(
            @PathVariable Long id,
            @RequestParam boolean isAvailable) {
        Technician technician = technicianService.updateAvailability(id, isAvailable);
        return ResponseEntity.ok(ApiResponse.success("Availability updated successfully", technician));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTechnician(@PathVariable Long id) {
        technicianService.deleteTechnician(id);
        return ResponseEntity.ok(ApiResponse.success("Technician deleted successfully"));
    }
}
