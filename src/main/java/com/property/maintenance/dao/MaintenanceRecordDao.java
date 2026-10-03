package com.property.maintenance.dao;

import com.property.maintenance.model.MaintenanceRecord;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MaintenanceRecordDao {
    MaintenanceRecord save(MaintenanceRecord record);
    Optional<MaintenanceRecord> findById(Long id);
    Optional<MaintenanceRecord> findByRequestId(Long requestId);
    List<MaintenanceRecord> findHistory(Long propertyId, Long tenantId, Long technicianId, LocalDateTime startDate, LocalDateTime endDate);
    List<MaintenanceRecord> findByPropertyId(Long propertyId);
    List<MaintenanceRecord> findByTenantId(Long tenantId);
    List<MaintenanceRecord> findByTechnicianId(Long technicianId);
}
