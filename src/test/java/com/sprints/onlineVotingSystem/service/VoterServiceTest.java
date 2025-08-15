package com.sprints.onlineVotingSystem.service;

import com.sprints.onlineVotingSystem.domain.Role;
import com.sprints.onlineVotingSystem.domain.Voter;
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
    void getVotersByCity_ShouldReturnEmptyList_WhenCityIsEmpty() {
        // Arrange
        String cityName = "";
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
    void getVotersByCity_ShouldReturnEmptyList_WhenCityIsNull() {
        // Arrange
        String cityName = null;
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
}
