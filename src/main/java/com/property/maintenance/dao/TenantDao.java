package com.property.maintenance.dao;

import com.property.maintenance.model.Tenant;
import java.util.List;
import java.util.Optional;

public interface TenantDao {
    Tenant save(Tenant tenant);
    Optional<Tenant> findById(Long id);
    Optional<Tenant> findByUserId(Long userId);
    Optional<Tenant> findByEmail(String email);
    List<Tenant> findAll(String status, Long propertyId);
    int assignProperty(Long tenantId, Long propertyId);
    int unassignProperty(Long tenantId);
    int deleteById(Long id);
    boolean existsByEmail(String email);
}
