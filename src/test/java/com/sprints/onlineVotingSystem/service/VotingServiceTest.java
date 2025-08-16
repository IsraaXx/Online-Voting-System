package com.sprints.onlineVotingSystem.service;

import com.sprints.onlineVotingSystem.domain.Candidate;
import com.sprints.onlineVotingSystem.domain.Election;
import com.sprints.onlineVotingSystem.domain.Role;
import com.sprints.onlineVotingSystem.domain.Vote;
import com.sprints.onlineVotingSystem.domain.Voter;
import com.sprints.onlineVotingSystem.dto.VoteRequestDTO;
import com.sprints.onlineVotingSystem.exception.BadRequestException;
import com.sprints.onlineVotingSystem.exception.ResourceNotFoundException;
import com.sprints.onlineVotingSystem.exception.UnassignedVoterException;
import com.sprints.onlineVotingSystem.exception.VotingClosedException;
import com.sprints.onlineVotingSystem.repository.CandidateRepository;
import com.sprints.onlineVotingSystem.repository.ElectionRepository;
import com.sprints.onlineVotingSystem.repository.VoteRepository;
import com.sprints.onlineVotingSystem.repository.VoterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotingServiceTest {

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private VoterRepository voterRepository;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private ElectionRepository electionRepository;

    @InjectMocks
    private VotingService votingService;

    private VoteRequestDTO validVoteRequest;
    private Voter validVoter;
    private Election validElection;
    private Candidate validCandidate;
    private Vote savedVote;

    @BeforeEach
    void setUp() {
        validVoteRequest = new VoteRequestDTO(1L, 1L);
        
        validVoter = new Voter();
        validVoter.setId(1L);
        validVoter.setEmail("test@example.com");
        validVoter.setName("Test Voter");
        validVoter.setCity("Test City");
        validVoter.setRole(Role.VOTER);
        
        validElection = new Election();
        validElection.setId(1L);
        validElection.setTitle("Test Election");
        validElection.setStartDate(LocalDate.now().minusDays(1));
        validElection.setEndDate(LocalDate.now().plusDays(1));
        
        validCandidate = new Candidate();
        validCandidate.setId(1L);
        validCandidate.setName("Test Candidate");
        validCandidate.setElection(validElection);
        
        savedVote = new Vote();
        savedVote.setId(1L);
        savedVote.setVoter(validVoter);
        savedVote.setCandidate(validCandidate);
        savedVote.setElection(validElection);
        savedVote.setVoteTime(LocalDateTime.now());
    }

    @Test
    void castVote_Success() {
        // Arrange
        when(voterRepository.findByEmail(validVoter.getEmail())).thenReturn(Optional.of(validVoter));
        when(electionRepository.findById(validVoteRequest.getElectionId())).thenReturn(Optional.of(validElection));
        when(candidateRepository.findById(validVoteRequest.getCandidateId())).thenReturn(Optional.of(validCandidate));
        when(voteRepository.existsByVoterAndElection(validVoter, validElection)).thenReturn(false);
        when(voteRepository.save(any(Vote.class))).thenReturn(savedVote);

        // Act
        Vote result = votingService.castVote(validVoteRequest, validVoter.getEmail());

        // Assert
        assertNotNull(result);
        assertEquals(savedVote.getId(), result.getId());
        verify(voteRepository).save(any(Vote.class));
    }

    @Test
    void castVote_NullCandidateId_ThrowsBadRequestException() {
        // Arrange
        VoteRequestDTO invalidRequest = new VoteRequestDTO(null, 1L);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            votingService.castVote(invalidRequest, validVoter.getEmail());
        });
        
        verify(voterRepository, never()).findByEmail(any());
    }

    @Test
    void castVote_NullElectionId_ThrowsBadRequestException() {
        // Arrange
        VoteRequestDTO invalidRequest = new VoteRequestDTO(1L, null);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            votingService.castVote(invalidRequest, validVoter.getEmail());
        });
        
        verify(voterRepository, never()).findByEmail(any());
    }

    @Test
    void castVote_VoterNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        when(voterRepository.findByEmail(validVoter.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            votingService.castVote(validVoteRequest, validVoter.getEmail());
        });
        
        verify(voterRepository).findByEmail(validVoter.getEmail());
        verify(electionRepository, never()).findById(any());
    }

    @Test
    void castVote_UnassignedVoter_ThrowsUnassignedVoterException() {
        // Arrange
        Voter unassignedVoter = new Voter();
        unassignedVoter.setId(1L);
        unassignedVoter.setEmail("unassigned@example.com");
        unassignedVoter.setName("Unassigned Voter");
        unassignedVoter.setCity(null); // No city assigned
        unassignedVoter.setRole(Role.VOTER);
        
        when(voterRepository.findByEmail(unassignedVoter.getEmail())).thenReturn(Optional.of(unassignedVoter));

        // Act & Assert
        assertThrows(UnassignedVoterException.class, () -> {
            votingService.castVote(validVoteRequest, unassignedVoter.getEmail());
        });
        
        verify(voterRepository).findByEmail(unassignedVoter.getEmail());
        verify(electionRepository, never()).findById(any());
    }

    @Test
    void castVote_EmptyCityVoter_ThrowsUnassignedVoterException() {
        // Arrange
        Voter emptyCityVoter = new Voter();
        emptyCityVoter.setId(1L);
        emptyCityVoter.setEmail("emptycity@example.com");
        emptyCityVoter.setName("Empty City Voter");
        emptyCityVoter.setCity(""); // Empty city
        emptyCityVoter.setRole(Role.VOTER);
        
        when(voterRepository.findByEmail(emptyCityVoter.getEmail())).thenReturn(Optional.of(emptyCityVoter));

        // Act & Assert
        assertThrows(UnassignedVoterException.class, () -> {
            votingService.castVote(validVoteRequest, emptyCityVoter.getEmail());
        });
        
        verify(voterRepository).findByEmail(emptyCityVoter.getEmail());
        verify(electionRepository, never()).findById(any());
    }

    @Test
    void castVote_ElectionNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        when(voterRepository.findByEmail(validVoter.getEmail())).thenReturn(Optional.of(validVoter));
        when(electionRepository.findById(validVoteRequest.getElectionId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            votingService.castVote(validVoteRequest, validVoter.getEmail());
        });
        
        verify(voterRepository).findByEmail(validVoter.getEmail());
        verify(electionRepository).findById(validVoteRequest.getElectionId());
        verify(candidateRepository, never()).findById(any());
    }

    @Test
    void castVote_VotingBeforeStartDate_ThrowsVotingClosedException() {
        // Arrange
        Election futureElection = new Election();
        futureElection.setId(1L);
        futureElection.setTitle("Future Election");
        futureElection.setStartDate(LocalDate.now().plusDays(1)); // Starts tomorrow
        futureElection.setEndDate(LocalDate.now().plusDays(2));
        
        when(voterRepository.findByEmail(validVoter.getEmail())).thenReturn(Optional.of(validVoter));
        when(electionRepository.findById(validVoteRequest.getElectionId())).thenReturn(Optional.of(futureElection));

        // Act & Assert
        assertThrows(VotingClosedException.class, () -> {
            votingService.castVote(validVoteRequest, validVoter.getEmail());
        });
        
        verify(voterRepository).findByEmail(validVoter.getEmail());
        verify(electionRepository).findById(validVoteRequest.getElectionId());
        verify(candidateRepository, never()).findById(any());
    }

    @Test
    void castVote_VotingAfterEndDate_ThrowsVotingClosedException() {
        // Arrange
        Election pastElection = new Election();
        pastElection.setId(1L);
        pastElection.setTitle("Past Election");
        pastElection.setStartDate(LocalDate.now().minusDays(2)); // Started 2 days ago
        pastElection.setEndDate(LocalDate.now().minusDays(1)); // Ended yesterday
        
        when(voterRepository.findByEmail(validVoter.getEmail())).thenReturn(Optional.of(validVoter));
        when(electionRepository.findById(validVoteRequest.getElectionId())).thenReturn(Optional.of(pastElection));

        // Act & Assert
        assertThrows(VotingClosedException.class, () -> {
            votingService.castVote(validVoteRequest, validVoter.getEmail());
        });
        
        verify(voterRepository).findByEmail(validVoter.getEmail());
        verify(electionRepository).findById(validVoteRequest.getElectionId());
        verify(candidateRepository, never()).findById(any());
    }

    @Test
    void castVote_ElectionWithNullDates_ThrowsBadRequestException() {
        // Arrange
        Election invalidElection = new Election();
        invalidElection.setId(1L);
        invalidElection.setTitle("Invalid Election");
        invalidElection.setStartDate(null);
        invalidElection.setEndDate(null);
        
        when(voterRepository.findByEmail(validVoter.getEmail())).thenReturn(Optional.of(validVoter));
        when(electionRepository.findById(validVoteRequest.getElectionId())).thenReturn(Optional.of(invalidElection));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            votingService.castVote(validVoteRequest, validVoter.getEmail());
        });
        
        verify(voterRepository).findByEmail(validVoter.getEmail());
        verify(electionRepository).findById(validVoteRequest.getElectionId());
        verify(candidateRepository, never()).findById(any());
    }

    @Test
    void castVote_CandidateNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        when(voterRepository.findByEmail(validVoter.getEmail())).thenReturn(Optional.of(validVoter));
        when(electionRepository.findById(validVoteRequest.getElectionId())).thenReturn(Optional.of(validElection));
        when(candidateRepository.findById(validVoteRequest.getCandidateId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            votingService.castVote(validVoteRequest, validVoter.getEmail());
        });
        
        verify(voterRepository).findByEmail(validVoter.getEmail());
        verify(electionRepository).findById(validVoteRequest.getElectionId());
        verify(candidateRepository).findById(validVoteRequest.getCandidateId());
        verify(voteRepository, never()).existsByVoterAndElection(any(), any());
    }

    @Test
    void castVote_CandidateWrongElection_ThrowsBadRequestException() {
        // Arrange
        Election wrongElection = new Election();
        wrongElection.setId(2L);
        wrongElection.setTitle("Wrong Election");
        wrongElection.setStartDate(LocalDate.now().minusDays(1));
        wrongElection.setEndDate(LocalDate.now().plusDays(1));
        
        Candidate wrongElectionCandidate = new Candidate();
        wrongElectionCandidate.setId(1L);
        wrongElectionCandidate.setName("Wrong Election Candidate");
        wrongElectionCandidate.setElection(wrongElection); // Different election
        
        when(voterRepository.findByEmail(validVoter.getEmail())).thenReturn(Optional.of(validVoter));
        when(electionRepository.findById(validVoteRequest.getElectionId())).thenReturn(Optional.of(validElection));
        when(candidateRepository.findById(validVoteRequest.getCandidateId())).thenReturn(Optional.of(wrongElectionCandidate));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            votingService.castVote(validVoteRequest, validVoter.getEmail());
        });
        
        verify(voterRepository).findByEmail(validVoter.getEmail());
        verify(electionRepository).findById(validVoteRequest.getElectionId());
        verify(candidateRepository).findById(validVoteRequest.getCandidateId());
        verify(voteRepository, never()).existsByVoterAndElection(any(), any());
    }

    @Test
    void castVote_DuplicateVote_ThrowsBadRequestException() {
        // Arrange
        when(voterRepository.findByEmail(validVoter.getEmail())).thenReturn(Optional.of(validVoter));
        when(electionRepository.findById(validVoteRequest.getElectionId())).thenReturn(Optional.of(validElection));
        when(candidateRepository.findById(validVoteRequest.getCandidateId())).thenReturn(Optional.of(validCandidate));
        when(voteRepository.existsByVoterAndElection(validVoter, validElection)).thenReturn(true);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            votingService.castVote(validVoteRequest, validVoter.getEmail());
        });
        
        verify(voterRepository).findByEmail(validVoter.getEmail());
        verify(electionRepository).findById(validVoteRequest.getElectionId());
        verify(candidateRepository).findById(validVoteRequest.getCandidateId());
        verify(voteRepository).existsByVoterAndElection(validVoter, validElection);
        verify(voteRepository, never()).save(any());
    }
}
