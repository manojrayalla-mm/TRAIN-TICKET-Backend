package com.property.maintenance.service.impl;

import com.property.maintenance.dao.MaintenanceRequestDao;
import com.property.maintenance.dao.PropertyDao;
import com.property.maintenance.dao.TechnicianDao;
import com.property.maintenance.dao.TenantDao;
import com.property.maintenance.dto.AssignTechnicianDto;
import com.property.maintenance.dto.MaintenanceCostDto;
import com.property.maintenance.dto.MaintenanceRequestDto;
import com.property.maintenance.dto.StatusUpdateDto;
import com.property.maintenance.exception.BadRequestException;
import com.property.maintenance.exception.ResourceNotFoundException;
import com.property.maintenance.model.MaintenanceRequest;
import com.property.maintenance.model.Technician;
import com.property.maintenance.service.MaintenanceCostService;
import com.property.maintenance.service.MaintenanceRecordService;
import com.property.maintenance.service.MaintenanceRequestService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
public class MaintenanceRequestServiceImpl implements MaintenanceRequestService {

    private final MaintenanceRequestDao requestDao;
    private final PropertyDao propertyDao;
    private final TenantDao tenantDao;
    private final TechnicianDao technicianDao;
    private final MaintenanceCostService costService;
    private final MaintenanceRecordService recordService;

    public MaintenanceRequestServiceImpl(MaintenanceRequestDao requestDao,
                                         PropertyDao propertyDao,
                                         TenantDao tenantDao,
                                         TechnicianDao technicianDao,
                                         MaintenanceCostService costService,
                                         MaintenanceRecordService recordService) {
        this.requestDao = requestDao;
        this.propertyDao = propertyDao;
        this.tenantDao = tenantDao;
        this.technicianDao = technicianDao;
        this.costService = costService;
        this.recordService = recordService;
    }

    @Override
    public MaintenanceRequest createRequest(MaintenanceRequestDto dto) {
        propertyDao.findById(dto.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property", "id", dto.getPropertyId()));

        tenantDao.findById(dto.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", "id", dto.getTenantId()));

        String ticketNumber = "TKT-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + (new Random().nextInt(9000) + 1000);

        MaintenanceRequest r = new MaintenanceRequest();
        r.setTicketNumber(ticketNumber);
        r.setPropertyId(dto.getPropertyId());
        r.setTenantId(dto.getTenantId());
        r.setCategory(dto.getCategory().toUpperCase());
        r.setPriority(dto.getPriority() != null ? dto.getPriority().toUpperCase() : "MEDIUM");
        r.setStatus("PENDING");
        r.setDescription(dto.getDescription());
        r.setRequestDate(LocalDateTime.now());
        r.setScheduledDate(dto.getScheduledDate());

        return requestDao.save(r);
    }

    @Override
    public List<MaintenanceRequest> getAllRequests(String status, String priority, Long propertyId, Long technicianId) {
        return requestDao.findAll(status, priority, propertyId, technicianId);
    }

    @Override
    public List<MaintenanceRequest> getRequestsByTenant(Long tenantId) {
        return requestDao.findByTenantId(tenantId);
    }

    @Override
    public List<MaintenanceRequest> getRequestsByTechnician(Long technicianId) {
        return requestDao.findByTechnicianId(technicianId);
    }

    @Override
    public MaintenanceRequest getRequestById(Long id) {
        return requestDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MaintenanceRequest", "id", id));
    }

    @Override
    public MaintenanceRequest getRequestByTicketNumber(String ticketNumber) {
        return requestDao.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new ResourceNotFoundException("MaintenanceRequest", "ticketNumber", ticketNumber));
    }

    @Override
    public MaintenanceRequest assignTechnician(Long id, AssignTechnicianDto dto) {
        MaintenanceRequest request = getRequestById(id);

        if ("COMPLETED".equalsIgnoreCase(request.getStatus()) || "CANCELLED".equalsIgnoreCase(request.getStatus())) {
            throw new BadRequestException("Cannot assign technician to a finished request");
        }

        Technician technician = technicianDao.findById(dto.getTechnicianId())
                .orElseThrow(() -> new ResourceNotFoundException("Technician", "id", dto.getTechnicianId()));

        requestDao.assignTechnician(id, dto.getTechnicianId(), dto.getScheduledDate(), dto.getNotes());
        technicianDao.updateAvailability(dto.getTechnicianId(), false);

        request.setTechnicianId(dto.getTechnicianId());
        request.setStatus("ASSIGNED");
        request.setScheduledDate(dto.getScheduledDate());
        if (dto.getNotes() != null) request.setNotes(dto.getNotes());

        return request;
    }

    @Override
    public MaintenanceRequest acceptRequest(Long id) {
        MaintenanceRequest request = getRequestById(id);

        if (!"ASSIGNED".equalsIgnoreCase(request.getStatus())) {
            throw new BadRequestException("Request must be in ASSIGNED status before it can be accepted and started");
        }

        request.setStatus("IN_PROGRESS");
        request.setStartedDate(LocalDateTime.now());
        requestDao.save(request);

        return request;
    }

    @Override
    public MaintenanceRequest updateStatus(Long id, StatusUpdateDto dto) {
        MaintenanceRequest request = getRequestById(id);
        String currentStatus = request.getStatus();
        String targetStatus = dto.getStatus().toUpperCase();

        if ("COMPLETED".equalsIgnoreCase(currentStatus) || "CANCELLED".equalsIgnoreCase(currentStatus)) {
            throw new BadRequestException("Finished requests cannot change status");
        }

        request.setStatus(targetStatus);
        if (dto.getNotes() != null) {
            request.setNotes(dto.getNotes());
        }

        if ("IN_PROGRESS".equalsIgnoreCase(targetStatus)) {
            request.setStartedDate(LocalDateTime.now());
            requestDao.save(request);
        } else if ("COMPLETED".equalsIgnoreCase(targetStatus)) {
            request.setCompletedDate(LocalDateTime.now());
            requestDao.save(request);

            if (request.getTechnicianId() != null) {
                technicianDao.updateAvailability(request.getTechnicianId(), true);
            }

            BigDecimal laborCost = dto.getLaborCost() != null ? dto.getLaborCost() : BigDecimal.ZERO;
            BigDecimal partsCost = dto.getPartsCost() != null ? dto.getPartsCost() : BigDecimal.ZERO;

            recordService.logRecord(request, dto.getWorkPerformed(), laborCost, partsCost, dto.getNotes());
            costService.setOrUpdateCost(new MaintenanceCostDto(request.getId(), laborCost, partsCost));

        } else if ("CANCELLED".equalsIgnoreCase(targetStatus)) {
            request.setCancellationReason(dto.getCancellationReason() != null ? dto.getCancellationReason() : "Cancelled by user");
            requestDao.save(request);

            if (request.getTechnicianId() != null) {
                technicianDao.updateAvailability(request.getTechnicianId(), true);
            }
        } else {
            requestDao.save(request);
        }

        return request;
    }

    @Override
    public MaintenanceRequest cancelRequest(Long id, String reason) {
        StatusUpdateDto dto = new StatusUpdateDto();
        dto.setStatus("CANCELLED");
        dto.setCancellationReason(reason);
        return updateStatus(id, dto);
    }
}
