package com.sprints.onlineVotingSystem.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BadRequestExceptionTest {

    @Test
    void constructor_WithMessage_ShouldSetMessage() {
        // Arrange & Act
        BadRequestException exception = new BadRequestException("Invalid input data");
        
        // Assert
        assertEquals("Invalid input data", exception.getMessage());
    }

    @Test
    void constructor_WithMessageAndCause_ShouldSetMessageAndCause() {
        // Arrange
        Throwable cause = new IllegalArgumentException("Validation failed");
        
        // Act
        BadRequestException exception = new BadRequestException("Invalid input data", cause);
        
        // Assert
        assertEquals("Invalid input data", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void constructor_WithEmptyMessage_ShouldHandleEmpty() {
        // Arrange & Act
        BadRequestException exception = new BadRequestException("");
        
        // Assert
        assertEquals("", exception.getMessage());
    }

    @Test
    void constructor_WithNullMessage_ShouldHandleNull() {
        // Arrange & Act
        BadRequestException exception = new BadRequestException(null);
        
        // Assert
        assertNull(exception.getMessage());
    }
}
