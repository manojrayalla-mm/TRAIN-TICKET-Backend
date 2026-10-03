package com.property.maintenance.service.impl;

import com.property.maintenance.dao.PropertyDao;
import com.property.maintenance.dao.TenantDao;
import com.property.maintenance.dto.TenantDto;
import com.property.maintenance.exception.BadRequestException;
import com.property.maintenance.exception.ResourceNotFoundException;
import com.property.maintenance.model.Tenant;
import com.property.maintenance.service.TenantService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TenantServiceImpl implements TenantService {

    private final TenantDao tenantDao;
    private final PropertyDao propertyDao;

    public TenantServiceImpl(TenantDao tenantDao, PropertyDao propertyDao) {
        this.tenantDao = tenantDao;
        this.propertyDao = propertyDao;
    }

    @Override
    public Tenant createTenant(TenantDto dto) {
        if (tenantDao.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Tenant email already exists: " + dto.getEmail());
        }

        if (dto.getPropertyId() != null) {
            propertyDao.findById(dto.getPropertyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Property", "id", dto.getPropertyId()));
            propertyDao.updateStatus(dto.getPropertyId(), "OCCUPIED");
        }

        Tenant tenant = new Tenant();
        tenant.setUserId(dto.getUserId());
        tenant.setFirstName(dto.getFirstName());
        tenant.setLastName(dto.getLastName());
        tenant.setEmail(dto.getEmail());
        tenant.setPhone(dto.getPhone());
        tenant.setEmergencyContact(dto.getEmergencyContact());
        tenant.setPropertyId(dto.getPropertyId());
        tenant.setLeaseStartDate(dto.getLeaseStartDate());
        tenant.setLeaseEndDate(dto.getLeaseEndDate());
        tenant.setStatus(dto.getStatus() != null ? dto.getStatus().toUpperCase() : "ACTIVE");

        return tenantDao.save(tenant);
    }

    @Override
    public List<Tenant> getAllTenants(String status, Long propertyId) {
        return tenantDao.findAll(status, propertyId);
    }

    @Override
    public Tenant getTenantById(Long id) {
        return tenantDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", "id", id));
    }

    @Override
    public Tenant getTenantByUserId(Long userId) {
        return tenantDao.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", "userId", userId));
    }

    @Override
    public Tenant updateTenant(Long id, TenantDto dto) {
        Tenant tenant = getTenantById(id);

        if (!tenant.getEmail().equalsIgnoreCase(dto.getEmail()) && tenantDao.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Email already taken: " + dto.getEmail());
        }

        tenant.setFirstName(dto.getFirstName());
        tenant.setLastName(dto.getLastName());
        tenant.setEmail(dto.getEmail());
        tenant.setPhone(dto.getPhone());
        tenant.setEmergencyContact(dto.getEmergencyContact());
        tenant.setLeaseStartDate(dto.getLeaseStartDate());
        tenant.setLeaseEndDate(dto.getLeaseEndDate());
        if (dto.getStatus() != null) {
            tenant.setStatus(dto.getStatus().toUpperCase());
        }

        if (dto.getPropertyId() != null && !dto.getPropertyId().equals(tenant.getPropertyId())) {
            propertyDao.findById(dto.getPropertyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Property", "id", dto.getPropertyId()));
            tenant.setPropertyId(dto.getPropertyId());
            propertyDao.updateStatus(dto.getPropertyId(), "OCCUPIED");
        }

        return tenantDao.save(tenant);
    }

    @Override
    public Tenant assignProperty(Long tenantId, Long propertyId) {
        Tenant tenant = getTenantById(tenantId);
        propertyDao.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property", "id", propertyId));

        tenantDao.assignProperty(tenantId, propertyId);
        propertyDao.updateStatus(propertyId, "OCCUPIED");

        tenant.setPropertyId(propertyId);
        return tenant;
    }

    @Override
    public Tenant unassignProperty(Long tenantId) {
        Tenant tenant = getTenantById(tenantId);
        if (tenant.getPropertyId() != null) {
            propertyDao.updateStatus(tenant.getPropertyId(), "AVAILABLE");
        }
        tenantDao.unassignProperty(tenantId);
        tenant.setPropertyId(null);
        return tenant;
    }

    @Override
    public void deleteTenant(Long id) {
        getTenantById(id);
        tenantDao.deleteById(id);
    }
}
