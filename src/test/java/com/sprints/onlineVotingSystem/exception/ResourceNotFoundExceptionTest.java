package com.sprints.onlineVotingSystem.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceNotFoundExceptionTest {

    @Test
    void constructor_WithMessage_ShouldSetMessage() {
        // Arrange & Act
        ResourceNotFoundException exception = new ResourceNotFoundException("Voter not found");
        
        // Assert
        assertEquals("Voter not found", exception.getMessage());
    }

    @Test
    void constructor_WithMessageAndCause_ShouldSetMessageAndCause() {
        // Arrange
        Throwable cause = new RuntimeException("Database error");
        
        // Act
        ResourceNotFoundException exception = new ResourceNotFoundException("Voter not found", cause);
        
        // Assert
        assertEquals("Voter not found", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void constructor_WithResourceNameFieldNameAndFieldValue_ShouldFormatMessage() {
        // Arrange & Act
        ResourceNotFoundException exception = new ResourceNotFoundException("Voter", "id", "123");
        
        // Assert
        assertEquals("Voter not found with id : '123'", exception.getMessage());
    }

    @Test
    void constructor_WithNullFieldValue_ShouldHandleNull() {
        // Arrange & Act
        ResourceNotFoundException exception = new ResourceNotFoundException("Voter", "email", null);
        
        // Assert
        assertEquals("Voter not found with email : 'null'", exception.getMessage());
    }

    @Test
    void constructor_WithEmptyFieldValue_ShouldHandleEmpty() {
        // Arrange & Act
        ResourceNotFoundException exception = new ResourceNotFoundException("Voter", "name", "");
        
        // Assert
        assertEquals("Voter not found with name : ''", exception.getMessage());
    }
}
