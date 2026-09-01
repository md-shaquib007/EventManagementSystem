package com.ceoms.dto;

import com.ceoms.dto.request.LoginRequest;
import com.ceoms.dto.request.RegisterRequest;
import com.ceoms.dto.response.ApiResponse;
import com.ceoms.dto.response.AuthResponse;
import com.ceoms.dto.response.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DtoTests {

    @Test
    @DisplayName("ApiResponse static factory methods test")
    void testApiResponseFactories() {
        ApiResponse<String> successResp = ApiResponse.success("Operation completed", "Sample Data");
        assertTrue(successResp.isSuccess());
        assertEquals("Operation completed", successResp.getMessage());
        assertEquals("Sample Data", successResp.getData());
        assertNotNull(successResp.getTimestamp());

        ApiResponse<Object> errorResp = ApiResponse.error("Resource not found");
        assertFalse(errorResp.isSuccess());
        assertEquals("Resource not found", errorResp.getMessage());
        assertNull(errorResp.getData());
        assertNotNull(errorResp.getTimestamp());
    }

    @Test
    @DisplayName("LoginRequest and RegisterRequest getters/setters test")
    void testRequestDtos() {
        LoginRequest login = new LoginRequest();
        login.setEmail("admin@ceoms.edu");
        login.setPassword("secret123");

        assertEquals("admin@ceoms.edu", login.getEmail());
        assertEquals("secret123", login.getPassword());

        RegisterRequest reg = new RegisterRequest();
        reg.setFirstName("Alex");
        reg.setLastName("Smith");
        reg.setEmail("alex@ceoms.edu");
        reg.setPassword("Password123!");

        assertEquals("Alex", reg.getFirstName());
        assertEquals("Smith", reg.getLastName());
        assertEquals("alex@ceoms.edu", reg.getEmail());
    }

    @Test
    @DisplayName("AuthResponse builder test")
    void testAuthResponse() {
        UserResponse user = UserResponse.builder()
                .id(5L)
                .email("user@ceoms.edu")
                .firstName("Alex")
                .lastName("Smith")
                .fullName("Alex Smith")
                .build();

        AuthResponse auth = AuthResponse.builder()
                .accessToken("mock-jwt-token")
                .refreshToken("mock-refresh-token")
                .tokenType("Bearer")
                .expiresIn(86400L)
                .user(user)
                .build();

        assertEquals("mock-jwt-token", auth.getAccessToken());
        assertEquals("Bearer", auth.getTokenType());
        assertEquals(86400L, auth.getExpiresIn());
        assertEquals(5L, auth.getUser().getId());
        assertEquals("user@ceoms.edu", auth.getUser().getEmail());
    }
}
