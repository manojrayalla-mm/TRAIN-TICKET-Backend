package com.property.maintenance.service;

import com.property.maintenance.dto.TenantDto;
import com.property.maintenance.model.Tenant;
import java.util.List;

public interface TenantService {
    Tenant createTenant(TenantDto dto);
    List<Tenant> getAllTenants(String status, Long propertyId);
    Tenant getTenantById(Long id);
    Tenant getTenantByUserId(Long userId);
    Tenant updateTenant(Long id, TenantDto dto);
    Tenant assignProperty(Long tenantId, Long propertyId);
    Tenant unassignProperty(Long tenantId);
    void deleteTenant(Long id);
}
