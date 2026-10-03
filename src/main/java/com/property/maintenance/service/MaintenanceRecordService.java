package com.property.maintenance.service;

import com.property.maintenance.model.MaintenanceRecord;
import com.property.maintenance.model.MaintenanceRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface MaintenanceRecordService {
    MaintenanceRecord logRecord(MaintenanceRequest request, String workPerformed, BigDecimal laborCost, BigDecimal partsCost, String remarks);
    List<MaintenanceRecord> getHistory(Long propertyId, Long tenantId, Long technicianId, LocalDateTime startDate, LocalDateTime endDate);
    List<MaintenanceRecord> getRecordsByProperty(Long propertyId);
    List<MaintenanceRecord> getRecordsByTenant(Long tenantId);
    List<MaintenanceRecord> getRecordsByTechnician(Long technicianId);
    MaintenanceRecord getRecordById(Long id);
    MaintenanceRecord getRecordByRequestId(Long requestId);
}
