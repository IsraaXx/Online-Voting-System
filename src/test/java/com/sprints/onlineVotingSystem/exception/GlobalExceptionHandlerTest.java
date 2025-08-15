package com.sprints.onlineVotingSystem.exception;

import com.sprints.onlineVotingSystem.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;
    
    @Mock
    private HttpServletRequest mockRequest;
    
    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
        mockRequest = new MockHttpServletRequest();
        ((MockHttpServletRequest) mockRequest).setRequestURI("/api/test");
    }

    @Test
    void handleResourceNotFoundException_ShouldReturn404Status() {
        // Arrange
        ResourceNotFoundException ex = new ResourceNotFoundException("Voter not found with id: 123");
        
        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleResourceNotFoundException(ex, mockRequest);
        
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Not Found", response.getBody().getError());
        assertEquals("Voter not found with id: 123", response.getBody().getMessage());
        assertEquals("/api/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleBadRequestException_ShouldReturn400Status() {
        // Arrange
        BadRequestException ex = new BadRequestException("Invalid input data");
        
        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleBadRequestException(ex, mockRequest);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Invalid input data", response.getBody().getMessage());
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    void handleValidationExceptions_ShouldReturn400StatusWithValidationErrors() {
        // Arrange
        FieldError fieldError = new FieldError("voter", "email", "Email is required");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));
        
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        
        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleValidationExceptions(ex, mockRequest);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Validation Error", response.getBody().getError());
        assertEquals("Invalid input parameters", response.getBody().getMessage());
        assertNotNull(response.getBody().getValidationErrors());
        assertEquals("Email is required", response.getBody().getValidationErrors().get("email"));
    }

    @Test
    void handleConstraintViolationException_ShouldReturn400Status() {
        // Arrange
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        ConstraintViolationException ex = new ConstraintViolationException("Validation failed", violations);
        
        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleConstraintViolationException(ex, mockRequest);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Constraint Violation", response.getBody().getError());
        assertEquals("Validation failed", response.getBody().getMessage());
    }

    @Test
    void handleMissingServletRequestParameterException_ShouldReturn400Status() {
        // Arrange
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("name", "String");
        
        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleMissingServletRequestParameterException(ex, mockRequest);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Missing Parameter", response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("name"));
    }

    @Test
    void handleMethodArgumentTypeMismatchException_ShouldReturn400Status() {
        // Arrange
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "123", Long.class, "id", null, null);
        
        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleMethodArgumentTypeMismatchException(ex, mockRequest);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Type Mismatch", response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("id"));
        assertTrue(response.getBody().getMessage().contains("Long"));
    }

    @Test
    void handleHttpMessageNotReadableException_ShouldReturn400Status() {
        // Arrange
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Invalid JSON", null, null);
        
        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleHttpMessageNotReadableException(ex, mockRequest);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Invalid Request Body", response.getBody().getError());
        assertEquals("The request body could not be parsed", response.getBody().getMessage());
    }

    @Test
    void handleDataIntegrityViolationException_ShouldReturn400Status() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Duplicate entry");
        
        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleDataIntegrityViolationException(ex, mockRequest);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Data Integrity Violation", response.getBody().getError());
        assertEquals("The operation would violate data integrity constraints", response.getBody().getMessage());
    }

    @Test
    void handleSQLException_ShouldReturn500Status() {
        // Arrange
        SQLException ex = new SQLException("Database connection failed");
        
        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleSQLException(ex, mockRequest);
        
        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Database Error", response.getBody().getError());
        assertEquals("An error occurred while accessing the database", response.getBody().getMessage());
    }

    @Test
    void handleNoHandlerFoundException_ShouldReturn404Status() {
        // Arrange
        NoHandlerFoundException ex = new NoHandlerFoundException("GET", "/api/nonexistent", null);
        
        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleNoHandlerFoundException(ex, mockRequest);
        
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Endpoint Not Found", response.getBody().getError());
        assertEquals("The requested endpoint does not exist", response.getBody().getMessage());
    }

    @Test
    void handleGenericException_ShouldReturn500Status() {
        // Arrange
        Exception ex = new RuntimeException("Unexpected error occurred");
        
        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleGenericException(ex, mockRequest);
        
        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
    }

    @Test
    void errorResponse_ShouldHaveConsistentStructure() {
        // Arrange
        ResourceNotFoundException ex = new ResourceNotFoundException("Test message");
        
        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleResourceNotFoundException(ex, mockRequest);
        ErrorResponse errorResponse = response.getBody();
        
        // Assert
        assertNotNull(errorResponse);
        assertNotNull(errorResponse.getTimestamp());
        assertEquals(404, errorResponse.getStatus());
        assertEquals("Not Found", errorResponse.getError());
        assertEquals("Test message", errorResponse.getMessage());
        assertEquals("/api/test", errorResponse.getPath());
        assertNull(errorResponse.getValidationErrors()); // Should be null for non-validation errors
    }
}
