package com.sprints.onlineVotingSystem.controller;

import com.sprints.onlineVotingSystem.domain.Role;
import com.sprints.onlineVotingSystem.domain.Voter;
import com.sprints.onlineVotingSystem.exception.ResourceNotFoundException;
import com.sprints.onlineVotingSystem.service.VoterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoterControllerTest {

    @Mock
    private VoterService voterService;

    @InjectMocks
    private VoterController voterController;

    private Voter testVoter1;
    private Voter testVoter2;

    @BeforeEach
    void setUp() {
        testVoter1 = Voter.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .passwordHash("hashedPassword123")
                .role(Role.VOTER)
                .city("New York")
                .build();

        testVoter2 = Voter.builder()
                .id(2L)
                .name("Jane Smith")
                .email("jane.smith@example.com")
                .passwordHash("hashedPassword456")
                .role(Role.VOTER)
                .city("New York")
                .build();
    }

    @Test
    void getVotersByCity_ShouldReturnVoters_WhenCityHasVoters() {
        // Arrange
        String cityName = "New York";
        List<Voter> expectedVoters = Arrays.asList(testVoter1, testVoter2);
        when(voterService.getVotersByCity(cityName)).thenReturn(expectedVoters);

        // Act
        ResponseEntity<List<Voter>> response = voterController.getVotersByCity(cityName);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedVoters, response.getBody());
        assertEquals(2, response.getBody().size());
        
        verify(voterService, times(1)).getVotersByCity(cityName);
    }

    @Test
    void getVotersByCity_ShouldReturnEmptyList_WhenCityHasNoVoters() {
        // Arrange
        String cityName = "Los Angeles";
        List<Voter> expectedVoters = Collections.emptyList();
        when(voterService.getVotersByCity(cityName)).thenReturn(expectedVoters);

        // Act
        ResponseEntity<List<Voter>> response = voterController.getVotersByCity(cityName);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedVoters, response.getBody());
        assertTrue(response.getBody().isEmpty());
        
        verify(voterService, times(1)).getVotersByCity(cityName);
    }

    @Test
    void getVotersByCity_ShouldReturnEmptyList_WhenCityIsEmpty() {
        // Arrange
        String cityName = "";
        List<Voter> expectedVoters = Collections.emptyList();
        when(voterService.getVotersByCity(cityName)).thenReturn(expectedVoters);

        // Act
        ResponseEntity<List<Voter>> response = voterController.getVotersByCity(cityName);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedVoters, response.getBody());
        assertTrue(response.getBody().isEmpty());
        
        verify(voterService, times(1)).getVotersByCity(cityName);
    }

    @Test
    void getVotersByCity_ShouldReturnEmptyList_WhenCityIsNull() {
        // Arrange
        String cityName = null;
        List<Voter> expectedVoters = Collections.emptyList();
        when(voterService.getVotersByCity(cityName)).thenReturn(expectedVoters);

        // Act
        ResponseEntity<List<Voter>> response = voterController.getVotersByCity(cityName);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedVoters, response.getBody());
        assertTrue(response.getBody().isEmpty());
        
        verify(voterService, times(1)).getVotersByCity(cityName);
    }
    
    @Test
    void getVoterById_ShouldReturnVoter_WhenVoterExists() {
        // Arrange
        Long voterId = 1L;
        when(voterService.getVoterById(voterId)).thenReturn(testVoter1);
        
        // Act
        ResponseEntity<Voter> response = voterController.getVoterById(voterId);
        
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testVoter1, response.getBody());
        
        verify(voterService, times(1)).getVoterById(voterId);
    }
    
    @Test
    void getVoterById_ShouldThrowResourceNotFoundException_WhenVoterDoesNotExist() {
        // Arrange
        Long voterId = 999L;
        when(voterService.getVoterById(voterId))
                .thenThrow(new ResourceNotFoundException("Voter", "id", voterId));
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> voterController.getVoterById(voterId));
        assertEquals("Voter not found with id : '999'", exception.getMessage());
        
        verify(voterService, times(1)).getVoterById(voterId);
    }
}
