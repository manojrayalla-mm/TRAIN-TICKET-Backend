package com.property.maintenance.dto;

import java.math.BigDecimal;

public class MaintenanceCostDto {
    private Long requestId;
    private BigDecimal laborCost;
    private BigDecimal partsCost;

    public MaintenanceCostDto() {
    }

    public MaintenanceCostDto(Long requestId, BigDecimal laborCost, BigDecimal partsCost) {
        this.requestId = requestId;
        this.laborCost = laborCost;
        this.partsCost = partsCost;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public BigDecimal getLaborCost() {
        return laborCost;
    }

    public void setLaborCost(BigDecimal laborCost) {
        this.laborCost = laborCost;
    }

    public BigDecimal getPartsCost() {
        return partsCost;
    }

    public void setPartsCost(BigDecimal partsCost) {
        this.partsCost = partsCost;
    }
}
