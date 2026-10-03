package com.property.maintenance.service;

import com.property.maintenance.dto.AssignTechnicianDto;
import com.property.maintenance.dto.MaintenanceRequestDto;
import com.property.maintenance.dto.StatusUpdateDto;
import com.property.maintenance.model.MaintenanceRequest;
import java.util.List;

public interface MaintenanceRequestService {
    MaintenanceRequest createRequest(MaintenanceRequestDto dto);
    List<MaintenanceRequest> getAllRequests(String status, String priority, Long propertyId, Long technicianId);
    List<MaintenanceRequest> getRequestsByTenant(Long tenantId);
    List<MaintenanceRequest> getRequestsByTechnician(Long technicianId);
    MaintenanceRequest getRequestById(Long id);
    MaintenanceRequest getRequestByTicketNumber(String ticketNumber);
    MaintenanceRequest assignTechnician(Long id, AssignTechnicianDto dto);
    MaintenanceRequest acceptRequest(Long id);
    MaintenanceRequest updateStatus(Long id, StatusUpdateDto dto);
    MaintenanceRequest cancelRequest(Long id, String reason);
}
