package com.property.maintenance.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Property {
    private Long id;
    private String propertyCode;
    private String title;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String propertyType; // APARTMENT, HOUSE, STUDIO, CONDO, TOWNHOUSE
    private String status;       // AVAILABLE, OCCUPIED, UNDER_MAINTENANCE
    private BigDecimal rentAmount;
    private Integer bedrooms;
    private Integer bathrooms;
    private String description;
    private LocalDateTime createdAt;

    public Property() {
    }

    public Property(Long id, String propertyCode, String title, String address, String city, 
                    String state, String zipCode, String propertyType, String status, 
                    BigDecimal rentAmount, Integer bedrooms, Integer bathrooms, 
                    String description, LocalDateTime createdAt) {
        this.id = id;
        this.propertyCode = propertyCode;
        this.title = title;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.propertyType = propertyType;
        this.status = status;
        this.rentAmount = rentAmount;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPropertyCode() {
        return propertyCode;
    }

    public void setPropertyCode(String propertyCode) {
        this.propertyCode = propertyCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getRentAmount() {
        return rentAmount;
    }

    public void setRentAmount(BigDecimal rentAmount) {
        this.rentAmount = rentAmount;
    }

    public Integer getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(Integer bedrooms) {
        this.bedrooms = bedrooms;
    }

    public Integer getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(Integer bathrooms) {
        this.bathrooms = bathrooms;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
