package com.property.maintenance.controller;

import com.property.maintenance.dto.ApiResponse;
import com.property.maintenance.model.MaintenanceRecord;
import com.property.maintenance.service.MaintenanceRecordService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/maintenance-history")
public class MaintenanceHistoryController {

    private final MaintenanceRecordService recordService;

    public MaintenanceHistoryController(MaintenanceRecordService recordService) {
        this.recordService = recordService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MaintenanceRecord>>> getHistory(
            @RequestParam(required = false) Long propertyId,
            @RequestParam(required = false) Long tenantId,
            @RequestParam(required = false) Long technicianId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<MaintenanceRecord> history = recordService.getHistory(propertyId, tenantId, technicianId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Maintenance history records fetched", history));
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<ApiResponse<List<MaintenanceRecord>>> getRecordsByProperty(@PathVariable Long propertyId) {
        List<MaintenanceRecord> records = recordService.getRecordsByProperty(propertyId);
        return ResponseEntity.ok(ApiResponse.success("Property maintenance history retrieved", records));
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<ApiResponse<List<MaintenanceRecord>>> getRecordsByTenant(@PathVariable Long tenantId) {
        List<MaintenanceRecord> records = recordService.getRecordsByTenant(tenantId);
        return ResponseEntity.ok(ApiResponse.success("Tenant maintenance history retrieved", records));
    }

    @GetMapping("/technician/{technicianId}")
    public ResponseEntity<ApiResponse<List<MaintenanceRecord>>> getRecordsByTechnician(@PathVariable Long technicianId) {
        List<MaintenanceRecord> records = recordService.getRecordsByTechnician(technicianId);
        return ResponseEntity.ok(ApiResponse.success("Technician work history retrieved", records));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MaintenanceRecord>> getRecordById(@PathVariable Long id) {
        MaintenanceRecord record = recordService.getRecordById(id);
        return ResponseEntity.ok(ApiResponse.success("Maintenance record details retrieved", record));
    }

    @GetMapping("/request/{requestId}")
    public ResponseEntity<ApiResponse<MaintenanceRecord>> getRecordByRequestId(@PathVariable Long requestId) {
        MaintenanceRecord record = recordService.getRecordByRequestId(requestId);
        return ResponseEntity.ok(ApiResponse.success("Maintenance record retrieved", record));
    }
}
