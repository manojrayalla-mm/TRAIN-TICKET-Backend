package com.property.maintenance.dto;

import java.time.LocalDateTime;

public class AssignTechnicianDto {
    private Long technicianId;
    private LocalDateTime scheduledDate;
    private String notes;

    public AssignTechnicianDto() {
    }

    public AssignTechnicianDto(Long technicianId, LocalDateTime scheduledDate, String notes) {
        this.technicianId = technicianId;
        this.scheduledDate = scheduledDate;
        this.notes = notes;
    }

    public Long getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(Long technicianId) {
        this.technicianId = technicianId;
    }

    public LocalDateTime getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDateTime scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
