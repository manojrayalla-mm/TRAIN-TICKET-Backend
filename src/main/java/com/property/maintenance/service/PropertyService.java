package com.property.maintenance.service;

import com.property.maintenance.dto.PropertyDto;
import com.property.maintenance.model.Property;
import java.util.List;

public interface PropertyService {
    Property createProperty(PropertyDto dto);
    List<Property> getAllProperties(String status, String propertyType, String city);
    Property getPropertyById(Long id);
    Property getPropertyByCode(String code);
    Property updateProperty(Long id, PropertyDto dto);
    Property updatePropertyStatus(Long id, String status);
    void deleteProperty(Long id);
}
