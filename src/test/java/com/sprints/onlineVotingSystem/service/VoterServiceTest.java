package com.sprints.onlineVotingSystem.service;

import com.sprints.onlineVotingSystem.domain.Role;
import com.sprints.onlineVotingSystem.domain.Voter;
import com.sprints.onlineVotingSystem.exception.BadRequestException;
import com.sprints.onlineVotingSystem.exception.ResourceNotFoundException;
import com.sprints.onlineVotingSystem.repository.VoterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoterServiceTest {

    @Mock
    private VoterRepository voterRepository;

    @InjectMocks
    private VoterService voterService;

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
        when(voterRepository.findByCity(cityName)).thenReturn(expectedVoters);

        // Act
        List<Voter> result = voterService.getVotersByCity(cityName);

        // Assert
        assertNotNull(result);
        assertEquals(expectedVoters, result);
        assertEquals(2, result.size());
        assertEquals("New York", result.get(0).getCity());
        assertEquals("New York", result.get(1).getCity());
        
        verify(voterRepository, times(1)).findByCity(cityName);
    }

    @Test
    void getVotersByCity_ShouldReturnEmptyList_WhenCityHasNoVoters() {
        // Arrange
        String cityName = "Los Angeles";
        List<Voter> expectedVoters = Collections.emptyList();
        when(voterRepository.findByCity(cityName)).thenReturn(expectedVoters);

        // Act
        List<Voter> result = voterService.getVotersByCity(cityName);

        // Assert
        assertNotNull(result);
        assertEquals(expectedVoters, result);
        assertTrue(result.isEmpty());
        
        verify(voterRepository, times(1)).findByCity(cityName);
    }

    @Test
    void getVotersByCity_ShouldThrowBadRequestException_WhenCityIsEmpty() {
        // Arrange
        String cityName = "";
        
        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, 
                () -> voterService.getVotersByCity(cityName));
        assertEquals("City name cannot be null or empty", exception.getMessage());
        
        verify(voterRepository, never()).findByCity(anyString());
    }

    @Test
    void getVotersByCity_ShouldThrowBadRequestException_WhenCityIsNull() {
        // Arrange
        String cityName = null;
        
        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, 
                () -> voterService.getVotersByCity(cityName));
        assertEquals("City name cannot be null or empty", exception.getMessage());
        
        verify(voterRepository, never()).findByCity(anyString());
    }
    
    @Test
    void getVotersByCity_ShouldThrowBadRequestException_WhenCityIsOnlyWhitespace() {
        // Arrange
        String cityName = "   ";
        
        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, 
                () -> voterService.getVotersByCity(cityName));
        assertEquals("City name cannot be null or empty", exception.getMessage());
        
        verify(voterRepository, never()).findByCity(anyString());
    }
    
    @Test
    void getVoterById_ShouldReturnVoter_WhenVoterExists() {
        // Arrange
        Long voterId = 1L;
        when(voterRepository.findById(voterId)).thenReturn(Optional.of(testVoter1));
        
        // Act
        Voter result = voterService.getVoterById(voterId);
        
        // Assert
        assertNotNull(result);
        assertEquals(testVoter1, result);
        verify(voterRepository, times(1)).findById(voterId);
    }
    
    @Test
    void getVoterById_ShouldThrowResourceNotFoundException_WhenVoterDoesNotExist() {
        // Arrange
        Long voterId = 999L;
        when(voterRepository.findById(voterId)).thenReturn(Optional.empty());
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> voterService.getVoterById(voterId));
        assertEquals("Voter not found with id : '999'", exception.getMessage());
        
        verify(voterRepository, times(1)).findById(voterId);
    }
}
