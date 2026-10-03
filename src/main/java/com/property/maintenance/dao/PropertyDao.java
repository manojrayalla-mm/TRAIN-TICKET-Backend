package com.property.maintenance.dao;

import com.property.maintenance.model.Property;
import java.util.List;
import java.util.Optional;

public interface PropertyDao {
    Property save(Property property);
    Optional<Property> findById(Long id);
    Optional<Property> findByPropertyCode(String code);
    List<Property> findAll(String status, String propertyType, String city);
    int updateStatus(Long id, String status);
    int deleteById(Long id);
    boolean existsByPropertyCode(String code);
}
