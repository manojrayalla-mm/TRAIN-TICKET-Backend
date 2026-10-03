package com.property.maintenance.controller;

import com.property.maintenance.dto.ApiResponse;
import com.property.maintenance.dto.TenantDto;
import com.property.maintenance.model.Tenant;
import com.property.maintenance.service.TenantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tenants")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Tenant>> createTenant(@RequestBody TenantDto dto) {
        Tenant tenant = tenantService.createTenant(dto);
        return new ResponseEntity<>(ApiResponse.success("Tenant created successfully", tenant), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Tenant>>> getAllTenants(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long propertyId) {
        List<Tenant> tenants = tenantService.getAllTenants(status, propertyId);
        return ResponseEntity.ok(ApiResponse.success("Tenants fetched successfully", tenants));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Tenant>> getTenantById(@PathVariable Long id) {
        Tenant tenant = tenantService.getTenantById(id);
        return ResponseEntity.ok(ApiResponse.success("Tenant fetched successfully", tenant));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Tenant>> getTenantByUserId(@PathVariable Long userId) {
        Tenant tenant = tenantService.getTenantByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Tenant fetched successfully", tenant));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Tenant>> updateTenant(
            @PathVariable Long id,
            @RequestBody TenantDto dto) {
        Tenant tenant = tenantService.updateTenant(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Tenant updated successfully", tenant));
    }

    @PostMapping("/{id}/assign-property/{propertyId}")
    public ResponseEntity<ApiResponse<Tenant>> assignProperty(
            @PathVariable Long id,
            @PathVariable Long propertyId) {
        Tenant tenant = tenantService.assignProperty(id, propertyId);
        return ResponseEntity.ok(ApiResponse.success("Tenant assigned to property successfully", tenant));
    }

    @PostMapping("/{id}/unassign-property")
    public ResponseEntity<ApiResponse<Tenant>> unassignProperty(@PathVariable Long id) {
        Tenant tenant = tenantService.unassignProperty(id);
        return ResponseEntity.ok(ApiResponse.success("Tenant unassigned from property", tenant));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTenant(@PathVariable Long id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.ok(ApiResponse.success("Tenant deleted successfully"));
    }
}
