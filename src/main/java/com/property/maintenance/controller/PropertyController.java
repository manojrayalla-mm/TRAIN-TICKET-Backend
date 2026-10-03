package com.property.maintenance.controller;

import com.property.maintenance.dto.ApiResponse;
import com.property.maintenance.dto.PropertyDto;
import com.property.maintenance.model.Property;
import com.property.maintenance.service.PropertyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Property>> createProperty(@RequestBody PropertyDto dto) {
        Property property = propertyService.createProperty(dto);
        return new ResponseEntity<>(ApiResponse.success("Property created successfully", property), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Property>>> getAllProperties(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String city) {
        List<Property> properties = propertyService.getAllProperties(status, type, city);
        return ResponseEntity.ok(ApiResponse.success("Properties fetched successfully", properties));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Property>> getPropertyById(@PathVariable Long id) {
        Property property = propertyService.getPropertyById(id);
        return ResponseEntity.ok(ApiResponse.success("Property fetched successfully", property));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<Property>> getPropertyByCode(@PathVariable String code) {
        Property property = propertyService.getPropertyByCode(code);
        return ResponseEntity.ok(ApiResponse.success("Property fetched successfully", property));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Property>> updateProperty(
            @PathVariable Long id,
            @RequestBody PropertyDto dto) {
        Property property = propertyService.updateProperty(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Property updated successfully", property));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Property>> updatePropertyStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        Property property = propertyService.updatePropertyStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Property status updated successfully", property));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProperty(@PathVariable Long id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.ok(ApiResponse.success("Property deleted successfully"));
    }
}
