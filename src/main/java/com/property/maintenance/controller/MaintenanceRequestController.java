package com.property.maintenance.controller;

import com.property.maintenance.dto.ApiResponse;
import com.property.maintenance.dto.AssignTechnicianDto;
import com.property.maintenance.dto.MaintenanceRequestDto;
import com.property.maintenance.dto.StatusUpdateDto;
import com.property.maintenance.model.MaintenanceRequest;
import com.property.maintenance.service.MaintenanceRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/maintenance-requests")
public class MaintenanceRequestController {

    private final MaintenanceRequestService requestService;

    public MaintenanceRequestController(MaintenanceRequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MaintenanceRequest>> createRequest(@RequestBody MaintenanceRequestDto dto) {
        MaintenanceRequest request = requestService.createRequest(dto);
        return new ResponseEntity<>(ApiResponse.success("Maintenance request created successfully", request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MaintenanceRequest>>> getAllRequests(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) Long propertyId,
            @RequestParam(required = false) Long technicianId) {
        List<MaintenanceRequest> requests = requestService.getAllRequests(status, priority, propertyId, technicianId);
        return ResponseEntity.ok(ApiResponse.success("Requests fetched successfully", requests));
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<ApiResponse<List<MaintenanceRequest>>> getRequestsByTenant(@PathVariable Long tenantId) {
        List<MaintenanceRequest> requests = requestService.getRequestsByTenant(tenantId);
        return ResponseEntity.ok(ApiResponse.success("Tenant requests fetched successfully", requests));
    }

    @GetMapping("/technician/{technicianId}")
    public ResponseEntity<ApiResponse<List<MaintenanceRequest>>> getRequestsByTechnician(@PathVariable Long technicianId) {
        List<MaintenanceRequest> requests = requestService.getRequestsByTechnician(technicianId);
        return ResponseEntity.ok(ApiResponse.success("Technician tasks fetched successfully", requests));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MaintenanceRequest>> getRequestById(@PathVariable Long id) {
        MaintenanceRequest request = requestService.getRequestById(id);
        return ResponseEntity.ok(ApiResponse.success("Request details fetched successfully", request));
    }

    @GetMapping("/ticket/{ticketNumber}")
    public ResponseEntity<ApiResponse<MaintenanceRequest>> getRequestByTicketNumber(@PathVariable String ticketNumber) {
        MaintenanceRequest request = requestService.getRequestByTicketNumber(ticketNumber);
        return ResponseEntity.ok(ApiResponse.success("Request details fetched successfully", request));
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<ApiResponse<MaintenanceRequest>> assignTechnician(
            @PathVariable Long id,
            @RequestBody AssignTechnicianDto dto) {
        MaintenanceRequest request = requestService.assignTechnician(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Technician assigned successfully", request));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<ApiResponse<MaintenanceRequest>> acceptRequest(@PathVariable Long id) {
        MaintenanceRequest request = requestService.acceptRequest(id);
        return ResponseEntity.ok(ApiResponse.success("Request accepted and marked IN_PROGRESS", request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<MaintenanceRequest>> updateStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateDto dto) {
        MaintenanceRequest request = requestService.updateStatus(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Status updated to " + dto.getStatus(), request));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<MaintenanceRequest>> cancelRequest(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "Cancelled by user") String reason) {
        MaintenanceRequest request = requestService.cancelRequest(id, reason);
        return ResponseEntity.ok(ApiResponse.success("Maintenance request cancelled", request));
    }
}
