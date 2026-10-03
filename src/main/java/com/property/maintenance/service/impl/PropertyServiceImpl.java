package com.property.maintenance.service.impl;

import com.property.maintenance.dao.PropertyDao;
import com.property.maintenance.dto.PropertyDto;
import com.property.maintenance.exception.BadRequestException;
import com.property.maintenance.exception.ResourceNotFoundException;
import com.property.maintenance.model.Property;
import com.property.maintenance.service.PropertyService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyServiceImpl implements PropertyService {

    private final PropertyDao propertyDao;

    public PropertyServiceImpl(PropertyDao propertyDao) {
        this.propertyDao = propertyDao;
    }

    @Override
    public Property createProperty(PropertyDto dto) {
        if (propertyDao.existsByPropertyCode(dto.getPropertyCode())) {
            throw new BadRequestException("Property code already exists: " + dto.getPropertyCode());
        }

        Property property = new Property();
        property.setPropertyCode(dto.getPropertyCode());
        property.setTitle(dto.getTitle());
        property.setAddress(dto.getAddress());
        property.setCity(dto.getCity());
        property.setState(dto.getState());
        property.setZipCode(dto.getZipCode());
        property.setPropertyType(dto.getPropertyType().toUpperCase());
        property.setStatus(dto.getStatus() != null ? dto.getStatus().toUpperCase() : "AVAILABLE");
        property.setRentAmount(dto.getRentAmount());
        property.setBedrooms(dto.getBedrooms());
        property.setBathrooms(dto.getBathrooms());
        property.setDescription(dto.getDescription());

        return propertyDao.save(property);
    }

    @Override
    public List<Property> getAllProperties(String status, String propertyType, String city) {
        return propertyDao.findAll(status, propertyType, city);
    }

    @Override
    public Property getPropertyById(Long id) {
        return propertyDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property", "id", id));
    }

    @Override
    public Property getPropertyByCode(String code) {
        return propertyDao.findByPropertyCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Property", "code", code));
    }

    @Override
    public Property updateProperty(Long id, PropertyDto dto) {
        Property property = getPropertyById(id);

        if (!property.getPropertyCode().equalsIgnoreCase(dto.getPropertyCode()) &&
                propertyDao.existsByPropertyCode(dto.getPropertyCode())) {
            throw new BadRequestException("Property code already in use: " + dto.getPropertyCode());
        }

        property.setPropertyCode(dto.getPropertyCode());
        property.setTitle(dto.getTitle());
        property.setAddress(dto.getAddress());
        property.setCity(dto.getCity());
        property.setState(dto.getState());
        property.setZipCode(dto.getZipCode());
        property.setPropertyType(dto.getPropertyType().toUpperCase());
        if (dto.getStatus() != null) {
            property.setStatus(dto.getStatus().toUpperCase());
        }
        property.setRentAmount(dto.getRentAmount());
        property.setBedrooms(dto.getBedrooms());
        property.setBathrooms(dto.getBathrooms());
        property.setDescription(dto.getDescription());

        return propertyDao.save(property);
    }

    @Override
    public Property updatePropertyStatus(Long id, String status) {
        Property property = getPropertyById(id);
        propertyDao.updateStatus(id, status.toUpperCase());
        property.setStatus(status.toUpperCase());
        return property;
    }

    @Override
    public void deleteProperty(Long id) {
        getPropertyById(id);
        propertyDao.deleteById(id);
    }
}
