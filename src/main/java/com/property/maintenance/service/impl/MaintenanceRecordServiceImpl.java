package com.property.maintenance.service.impl;

import com.property.maintenance.dao.MaintenanceRecordDao;
import com.property.maintenance.exception.ResourceNotFoundException;
import com.property.maintenance.model.MaintenanceRecord;
import com.property.maintenance.model.MaintenanceRequest;
import com.property.maintenance.service.MaintenanceRecordService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MaintenanceRecordServiceImpl implements MaintenanceRecordService {

    private final MaintenanceRecordDao recordDao;

    public MaintenanceRecordServiceImpl(MaintenanceRecordDao recordDao) {
        this.recordDao = recordDao;
    }

    @Override
    public MaintenanceRecord logRecord(MaintenanceRequest request, String workPerformed,
                                       BigDecimal laborCost, BigDecimal partsCost, String remarks) {
        MaintenanceRecord record = recordDao.findByRequestId(request.getId())
                .orElseGet(() -> {
                    MaintenanceRecord rec = new MaintenanceRecord();
                    rec.setRequestId(request.getId());
                    rec.setPropertyId(request.getPropertyId());
                    rec.setTenantId(request.getTenantId());
                    rec.setMaintenanceDate(request.getRequestDate());
                    return rec;
                });

        record.setTechnicianId(request.getTechnicianId());
        record.setProblemDescription(request.getDescription());
        record.setWorkPerformed(workPerformed != null && !workPerformed.isBlank() ? workPerformed : "Maintenance work performed and inspected.");
        record.setCompletionDate(request.getCompletedDate() != null ? request.getCompletedDate() : LocalDateTime.now());
        record.setLaborCost(laborCost != null ? laborCost : BigDecimal.ZERO);
        record.setPartsCost(partsCost != null ? partsCost : BigDecimal.ZERO);
        record.setTotalCost(record.getLaborCost().add(record.getPartsCost()));
        record.setStatus("COMPLETED");
        record.setRemarks(remarks != null ? remarks : request.getNotes());

        return recordDao.save(record);
    }

    @Override
    public List<MaintenanceRecord> getHistory(Long propertyId, Long tenantId, Long technicianId, LocalDateTime startDate, LocalDateTime endDate) {
        return recordDao.findHistory(propertyId, tenantId, technicianId, startDate, endDate);
    }

    @Override
    public List<MaintenanceRecord> getRecordsByProperty(Long propertyId) {
        return recordDao.findByPropertyId(propertyId);
    }

    @Override
    public List<MaintenanceRecord> getRecordsByTenant(Long tenantId) {
        return recordDao.findByTenantId(tenantId);
    }

    @Override
    public List<MaintenanceRecord> getRecordsByTechnician(Long technicianId) {
        return recordDao.findByTechnicianId(technicianId);
    }

    @Override
    public MaintenanceRecord getRecordById(Long id) {
        return recordDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MaintenanceRecord", "id", id));
    }

    @Override
    public MaintenanceRecord getRecordByRequestId(Long requestId) {
        return recordDao.findByRequestId(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("MaintenanceRecord", "requestId", requestId));
    }
}
