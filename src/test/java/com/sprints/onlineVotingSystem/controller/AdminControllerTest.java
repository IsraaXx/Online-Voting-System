package com.sprints.onlineVotingSystem.controller;

import com.sprints.onlineVotingSystem.dto.CandidateResultDTO;
import com.sprints.onlineVotingSystem.service.ElectionResultService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private ElectionResultService electionResultService;

    @InjectMocks
    private AdminController adminController;

    private List<CandidateResultDTO> mockResults;

    @BeforeEach
    void setUp() {
        mockResults = Arrays.asList(
            new CandidateResultDTO("Candidate A", 150L),
            new CandidateResultDTO("Candidate B", 120L),
            new CandidateResultDTO("Candidate C", 80L)
        );
    }

    @Test
    void getElectionResults_Success() {
        // Arrange
        when(electionResultService.getElectionResults()).thenReturn(mockResults);

        // Act
        ResponseEntity<List<CandidateResultDTO>> response = adminController.getElectionResults();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());
        assertEquals("Candidate A", response.getBody().get(0).getCandidateName());
        assertEquals(150L, response.getBody().get(0).getTotalVotes());
        verify(electionResultService, times(1)).getElectionResults();
    }

    @Test
    void getElectionResults_ServiceThrowsException() {
        // Arrange
        when(electionResultService.getElectionResults()).thenThrow(new RuntimeException("Service error"));

        // Act
        ResponseEntity<List<CandidateResultDTO>> response = adminController.getElectionResults();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(electionResultService, times(1)).getElectionResults();
    }
}
