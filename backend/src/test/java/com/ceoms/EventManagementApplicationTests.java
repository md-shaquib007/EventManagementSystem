package com.ceoms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class EventManagementApplicationTests {

    @Test
    @DisplayName("Spring Context loads successfully with H2 test profile")
    void contextLoads() {
        // Verifies Spring Boot application context startup and bean initialization
    }
}
