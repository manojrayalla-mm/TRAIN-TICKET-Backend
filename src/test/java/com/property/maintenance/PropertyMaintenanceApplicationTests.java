package com.property.maintenance;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class PropertyMaintenanceApplicationTests {

    @Test
    void contextLoads() {
        // Verifies Spring context, JDBC DAOs, Services, and Controllers load successfully
    }
}
