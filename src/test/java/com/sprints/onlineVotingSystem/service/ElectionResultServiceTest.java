package com.sprints.onlineVotingSystem.service;

import com.sprints.onlineVotingSystem.dto.CandidateResultDTO;
import com.sprints.onlineVotingSystem.repository.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ElectionResultServiceTest {

    @Mock
    private VoteRepository voteRepository;

    @InjectMocks
    private ElectionResultService electionResultService;

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
        when(voteRepository.getCandidateVoteCounts()).thenReturn(mockResults);

        // Act
        List<CandidateResultDTO> results = electionResultService.getElectionResults();

        // Assert
        assertNotNull(results);
        assertEquals(3, results.size());
        assertEquals("Candidate A", results.get(0).getCandidateName());
        assertEquals(150L, results.get(0).getTotalVotes());
        verify(voteRepository, times(1)).getCandidateVoteCounts();
    }

    @Test
    void getElectionResults_RepositoryThrowsException() {
        // Arrange
        when(voteRepository.getCandidateVoteCounts()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            electionResultService.getElectionResults();
        });
        verify(voteRepository, times(1)).getCandidateVoteCounts();
    }
}
