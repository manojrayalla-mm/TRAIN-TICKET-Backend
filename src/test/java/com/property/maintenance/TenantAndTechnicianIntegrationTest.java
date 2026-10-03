package com.property.maintenance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.property.maintenance.dto.TechnicianDto;
import com.property.maintenance.dto.TenantDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class TenantAndTechnicianIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllTenants() throws Exception {
        mockMvc.perform(get("/tenants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetTenantById() throws Exception {
        mockMvc.perform(get("/tenants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.firstName").value("John"));
    }

    @Test
    void testCreateTenant() throws Exception {
        TenantDto tenantDto = new TenantDto();
        tenantDto.setFirstName("Alice");
        tenantDto.setLastName("Wonder");
        tenantDto.setEmail("alice.wonder.example.com");
        tenantDto.setPhone("555-9000");
        tenantDto.setEmergencyContact("Bob Wonder (555-9001)");
        tenantDto.setLeaseStartDate(LocalDate.of(2026, 5, 1));
        tenantDto.setLeaseEndDate(LocalDate.of(2027, 4, 30));
        tenantDto.setStatus("ACTIVE");

        mockMvc.perform(post("/tenants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tenantDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("alice.wonder.example.com"));
    }

    @Test
    void testGetAllTechnicians() throws Exception {
        mockMvc.perform(get("/technicians"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetTechniciansBySpecialization() throws Exception {
        mockMvc.perform(get("/technicians/available/PLUMBING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testCreateTechnician() throws Exception {
        TechnicianDto techDto = new TechnicianDto();
        techDto.setFirstName("Carlos");
        techDto.setLastName("Santos");
        techDto.setEmail("carlos.santos.example.com");
        techDto.setPhone("555-8888");
        techDto.setSpecialization("CARPENTRY");
        techDto.setHourlyRate(new BigDecimal("70.00"));
        techDto.setIsAvailable(true);
        techDto.setStatus("ACTIVE");

        mockMvc.perform(post("/technicians")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(techDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.specialization").value("CARPENTRY"));
    }

    @Test
    void testToggleTechnicianAvailability() throws Exception {
        mockMvc.perform(patch("/technicians/1/availability")
                .param("isAvailable", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.isAvailable").value(false));
    }
}
