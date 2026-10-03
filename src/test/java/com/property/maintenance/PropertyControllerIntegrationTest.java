package com.property.maintenance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.property.maintenance.dto.PropertyDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class PropertyControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllProperties() throws Exception {
        mockMvc.perform(get("/properties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetPropertyById() throws Exception {
        mockMvc.perform(get("/properties/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.propertyCode").value("PROP-101"));
    }

    @Test
    void testGetPropertyByCode() throws Exception {
        mockMvc.perform(get("/properties/code/PROP-101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void testCreateProperty() throws Exception {
        PropertyDto dto = new PropertyDto();
        dto.setPropertyCode("PROP-999");
        dto.setTitle("Test Penthouse");
        dto.setAddress("99 Sky Tower Way");
        dto.setCity("Seattle");
        dto.setState("WA");
        dto.setZipCode("98101");
        dto.setPropertyType("CONDO");
        dto.setStatus("AVAILABLE");
        dto.setRentAmount(new BigDecimal("3500.00"));
        dto.setBedrooms(3);
        dto.setBathrooms(2);
        dto.setDescription("Luxury high rise penthouse");

        mockMvc.perform(post("/properties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.propertyCode").value("PROP-999"));
    }

    @Test
    void testUpdatePropertyStatus() throws Exception {
        mockMvc.perform(patch("/properties/3/status")
                .param("status", "UNDER_MAINTENANCE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("UNDER_MAINTENANCE"));
    }
}
