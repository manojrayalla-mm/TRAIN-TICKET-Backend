package com.property.maintenance.dao;

import com.property.maintenance.model.MaintenanceRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MaintenanceRequestDao {
    MaintenanceRequest save(MaintenanceRequest request);
    Optional<MaintenanceRequest> findById(Long id);
    Optional<MaintenanceRequest> findByTicketNumber(String ticketNumber);
    List<MaintenanceRequest> findAll(String status, String priority, Long propertyId, Long technicianId);
    List<MaintenanceRequest> findByTenantId(Long tenantId);
    List<MaintenanceRequest> findByTechnicianId(Long technicianId);
    int updateStatus(Long id, String status, String notes);
    int assignTechnician(Long id, Long technicianId, LocalDateTime scheduledDate, String notes);
    int markCompleted(Long id, LocalDateTime completedDate, String notes);
    int markCancelled(Long id, String reason);
}
