package com.sprints.onlineVotingSystem.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void defaultConstructor_ShouldSetTimestamp() {
        // Arrange & Act
        ErrorResponse errorResponse = new ErrorResponse();
        
        // Assert
        assertNotNull(errorResponse.getTimestamp());
        assertTrue(errorResponse.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(errorResponse.getTimestamp().isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @Test
    void constructor_WithStatusErrorAndMessage_ShouldSetFields() {
        // Arrange & Act
        ErrorResponse errorResponse = new ErrorResponse(404, "Not Found", "Resource not found");
        
        // Assert
        assertEquals(404, errorResponse.getStatus());
        assertEquals("Not Found", errorResponse.getError());
        assertEquals("Resource not found", errorResponse.getMessage());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    void constructor_WithStatusErrorMessageAndPath_ShouldSetAllFields() {
        // Arrange & Act
        ErrorResponse errorResponse = new ErrorResponse(400, "Bad Request", "Invalid input", "/api/test");
        
        // Assert
        assertEquals(400, errorResponse.getStatus());
        assertEquals("Bad Request", errorResponse.getError());
        assertEquals("Invalid input", errorResponse.getMessage());
        assertEquals("/api/test", errorResponse.getPath());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        ErrorResponse errorResponse = new ErrorResponse();
        LocalDateTime timestamp = LocalDateTime.now();
        Map<String, String> validationErrors = new HashMap<>();
        validationErrors.put("email", "Email is required");
        
        // Act
        errorResponse.setStatus(500);
        errorResponse.setError("Internal Server Error");
        errorResponse.setMessage("Something went wrong");
        errorResponse.setPath("/api/error");
        errorResponse.setTimestamp(timestamp);
        errorResponse.setValidationErrors(validationErrors);
        
        // Assert
        assertEquals(500, errorResponse.getStatus());
        assertEquals("Internal Server Error", errorResponse.getError());
        assertEquals("Something went wrong", errorResponse.getMessage());
        assertEquals("/api/error", errorResponse.getPath());
        assertEquals(timestamp, errorResponse.getTimestamp());
        assertEquals(validationErrors, errorResponse.getValidationErrors());
    }

    @Test
    void validationErrors_ShouldBeIncludedWhenSet() {
        // Arrange
        ErrorResponse errorResponse = new ErrorResponse();
        Map<String, String> validationErrors = new HashMap<>();
        validationErrors.put("name", "Name is required");
        validationErrors.put("email", "Email is invalid");
        
        // Act
        errorResponse.setValidationErrors(validationErrors);
        
        // Assert
        assertNotNull(errorResponse.getValidationErrors());
        assertEquals(2, errorResponse.getValidationErrors().size());
        assertEquals("Name is required", errorResponse.getValidationErrors().get("name"));
        assertEquals("Email is invalid", errorResponse.getValidationErrors().get("email"));
    }

    @Test
    void validationErrors_ShouldBeNullByDefault() {
        // Arrange & Act
        ErrorResponse errorResponse = new ErrorResponse();
        
        // Assert
        assertNull(errorResponse.getValidationErrors());
    }

    @Test
    void path_ShouldBeNullByDefault() {
        // Arrange & Act
        ErrorResponse errorResponse = new ErrorResponse(500, "Error", "Message");
        
        // Assert
        assertNull(errorResponse.getPath());
    }
}
