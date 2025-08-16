package com.sprints.onlineVotingSystem.service;

import com.sprints.onlineVotingSystem.domain.Candidate;
import com.sprints.onlineVotingSystem.domain.Election;
import com.sprints.onlineVotingSystem.dto.CandidateDTO;
import com.sprints.onlineVotingSystem.repository.CandidateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateServiceTest {

    @Mock
    private CandidateRepository candidateRepository;

    @InjectMocks
    private CandidateService candidateService;

    private List<Candidate> mockCandidates;
    private Election mockElection;

    @BeforeEach
    void setUp() {
        mockElection = new Election(1L, "Presidential Election 2024", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));

        mockCandidates = Arrays.asList(
            new Candidate(1L, "Candidate A", mockElection),
            new Candidate(2L, "Candidate B", mockElection),
            new Candidate(3L, "Candidate C", mockElection)
        );
    }

    @Test
    void getAllCandidates_Success() {
        // Arrange
        when(candidateRepository.findAllWithElection()).thenReturn(mockCandidates);

        // Act
        List<CandidateDTO> result = candidateService.getAllCandidates();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        
        // Verify first candidate
        CandidateDTO firstCandidate = result.get(0);
        assertEquals(1L, firstCandidate.getId());
        assertEquals("Candidate A", firstCandidate.getName());
        assertEquals("Independent", firstCandidate.getParty()); // Default party
        assertEquals("Presidential Election 2024", firstCandidate.getElectionName());
        
        // Verify second candidate
        CandidateDTO secondCandidate = result.get(1);
        assertEquals(2L, secondCandidate.getId());
        assertEquals("Candidate B", secondCandidate.getName());
        assertEquals("Independent", secondCandidate.getParty());
        assertEquals("Presidential Election 2024", secondCandidate.getElectionName());
        
        verify(candidateRepository).findAllWithElection();
    }

    @Test
    void getAllCandidates_EmptyList() {
        // Arrange
        when(candidateRepository.findAllWithElection()).thenReturn(Arrays.asList());

        // Act
        List<CandidateDTO> result = candidateService.getAllCandidates();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(candidateRepository).findAllWithElection();
    }

    @Test
    void getAllCandidates_CandidateWithoutElection() {
        // Arrange
        Candidate candidateWithoutElection = new Candidate(4L, "Candidate D", null);
        
        when(candidateRepository.findAllWithElection()).thenReturn(Arrays.asList(candidateWithoutElection));

        // Act
        List<CandidateDTO> result = candidateService.getAllCandidates();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        
        CandidateDTO candidateDTO = result.get(0);
        assertEquals(4L, candidateDTO.getId());
        assertEquals("Candidate D", candidateDTO.getName());
        assertEquals("Independent", candidateDTO.getParty());
        assertEquals("Unknown Election", candidateDTO.getElectionName());
        
        verify(candidateRepository).findAllWithElection();
    }

    @Test
    void getAllCandidates_RepositoryThrowsException() {
        // Arrange
        when(candidateRepository.findAllWithElection()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            candidateService.getAllCandidates();
        });
        
        verify(candidateRepository).findAllWithElection();
    }
}
