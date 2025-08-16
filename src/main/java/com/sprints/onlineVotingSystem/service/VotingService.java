package com.sprints.onlineVotingSystem.service;

import com.sprints.onlineVotingSystem.domain.Candidate;
import com.sprints.onlineVotingSystem.domain.Election;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class VotingService {
    
    private final VoteRepository voteRepository;
    private final VoterRepository voterRepository;
    private final CandidateRepository candidateRepository;
    private final ElectionRepository electionRepository;
    
    /**
     * Casts a vote for a candidate in an election with various restrictions
     * 
     * @param voteRequest The voting request containing candidate and election IDs
     * @param voterEmail The email of the authenticated voter
     * @return The created Vote entity
     * @throws VotingClosedException if voting is outside the allowed time window
     * @throws UnassignedVoterException if the voter is not assigned to a city
     * @throws BadRequestException if the request is invalid
     * @throws ResourceNotFoundException if resources are not found
     */
    @Transactional
    public Vote castVote(VoteRequestDTO voteRequest, String voterEmail) {
        log.info("Voter {} attempting to cast vote for candidate {} in election {}", 
                voterEmail, voteRequest.getCandidateId(), voteRequest.getElectionId());
        
        // Validate input
        validateVoteRequest(voteRequest);
        
        // Get voter
        Voter voter = getVoterByEmail(voterEmail);
        
        // Check if voter is assigned to a city
        validateVoterAssignment(voter);
        
        // Get election and validate voting window
        Election election = getElectionById(voteRequest.getElectionId());
        validateVotingWindow(election);
        
        // Get candidate and validate it belongs to the election
        Candidate candidate = getCandidateById(voteRequest.getCandidateId());
        validateCandidateElection(candidate, election);
        
        // Check if voter has already voted in this election
        checkDuplicateVote(voter, election);
        
        // Create and save the vote
        Vote vote = createVote(voter, candidate, election);
        
        log.info("Vote successfully cast by voter {} for candidate {} in election {}", 
                voterEmail, candidate.getName(), election.getTitle());
        
        return vote;
    }
    
    /**
     * Validates the vote request input
     */
    private void validateVoteRequest(VoteRequestDTO voteRequest) {
        if (voteRequest.getCandidateId() == null) {
            throw new BadRequestException("Candidate ID is required");
        }
        if (voteRequest.getElectionId() == null) {
            throw new BadRequestException("Election ID is required");
        }
    }
    
    /**
     * Gets voter by email or throws exception if not found
     */
    private Voter getVoterByEmail(String email) {
        return voterRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Voter not found with email: " + email));
    }
    
    /**
     * Validates that the voter is assigned to a city
     */
    private void validateVoterAssignment(Voter voter) {
        if (voter.getCity() == null || voter.getCity().trim().isEmpty()) {
            log.warn("Unassigned voter {} attempted to vote", voter.getEmail());
            throw new UnassignedVoterException("Voter must be assigned to a city before voting. Please contact your election administrator.");
        }
        log.debug("Voter {} is assigned to city: {}", voter.getEmail(), voter.getCity());
    }
    
    /**
     * Gets election by ID or throws exception if not found
     */
    private Election getElectionById(Long electionId) {
        return electionRepository.findById(electionId)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with ID: " + electionId));
    }
    
    /**
     * Validates that the current time is within the voting window
     */
    private void validateVotingWindow(Election election) {
        LocalDateTime now = LocalDateTime.now();
        
        if (election.getStartDate() == null || election.getEndDate() == null) {
            throw new BadRequestException("Election dates are not properly configured");
        }
        
        if (now.isBefore(election.getStartDate().atStartOfDay())) {
            log.warn("Voting attempted before election start date: {} (current: {})", 
                    election.getStartDate(), now.toLocalDate());
            throw new VotingClosedException("Voting has not started yet. Election begins on " + election.getStartDate());
        }
        
        if (now.isAfter(election.getEndDate().atTime(23, 59, 59))) {
            log.warn("Voting attempted after election end date: {} (current: {})", 
                    election.getEndDate(), now.toLocalDate());
            throw new VotingClosedException("Voting has ended. Election closed on " + election.getEndDate());
        }
        
        log.debug("Voting window validation passed for election: {} (current: {})", 
                election.getTitle(), now);
    }
    
    /**
     * Gets candidate by ID or throws exception if not found
     */
    private Candidate getCandidateById(Long candidateId) {
        return candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with ID: " + candidateId));
    }
    
    /**
     * Validates that the candidate belongs to the specified election
     */
    private void validateCandidateElection(Candidate candidate, Election election) {
        if (!election.getId().equals(candidate.getElection().getId())) {
            log.warn("Voter attempted to vote for candidate {} in wrong election {}", 
                    candidate.getName(), election.getTitle());
            throw new BadRequestException("Candidate does not belong to the specified election");
        }
    }
    
    /**
     * Checks if the voter has already voted in this election
     */
    private void checkDuplicateVote(Voter voter, Election election) {
        boolean hasVoted = voteRepository.existsByVoterAndElection(voter, election);
        if (hasVoted) {
            log.warn("Voter {} attempted to vote again in election {}", voter.getEmail(), election.getTitle());
            throw new BadRequestException("You have already voted in this election");
        }
    }
    
    /**
     * Creates and saves the vote
     */
    private Vote createVote(Voter voter, Candidate candidate, Election election) {
        Vote vote = Vote.builder()
                .voter(voter)
                .candidate(candidate)
                .election(election)
                .voteTime(LocalDateTime.now())
                .build();
        
        return voteRepository.save(vote);
    }
}
