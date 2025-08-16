package com.sprints.onlineVotingSystem.controller;

import com.sprints.onlineVotingSystem.domain.Voter;
import com.sprints.onlineVotingSystem.domain.Vote;
import com.sprints.onlineVotingSystem.dto.CandidateDTO;
import com.sprints.onlineVotingSystem.dto.LoginRequestDTO;
import com.sprints.onlineVotingSystem.dto.LoginResponseDTO;
import com.sprints.onlineVotingSystem.dto.VoteRequestDTO;
import com.sprints.onlineVotingSystem.service.AuthService;
import com.sprints.onlineVotingSystem.service.CandidateService;
import com.sprints.onlineVotingSystem.service.VoterService;
import com.sprints.onlineVotingSystem.service.VotingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/voters")
@RequiredArgsConstructor
@Slf4j
public class VoterController {

    private final VoterService voterService;
    private final AuthService authService;
    private final CandidateService candidateService;
    private final VotingService votingService;

    /**
     * Get all voters filtered by city
     * @param cityName the city name to filter by
     * @return list of voters in the specified city
     */
    @GetMapping("/city/{cityName}")
    public ResponseEntity<List<Voter>> getVotersByCity(@PathVariable String cityName) {
        List<Voter> voters = voterService.getVotersByCity(cityName);
        return ResponseEntity.ok(voters);
    }
    
    /**
     * Get voter by ID
     * @param id the voter ID
     * @return the voter if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Voter> getVoterById(@PathVariable Long id) {
        Voter voter = voterService.getVoterById(id);
        return ResponseEntity.ok(voter);
    }
    
    /**
     * Voter login endpoint
     * Authenticates voter credentials and returns JWT token
     * 
     * @param loginRequest The login credentials
     * @return LoginResponseDTO containing JWT token and user info
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        log.info("Voter login attempt for email: {}", loginRequest.getEmail());
        try {
            LoginResponseDTO response = authService.authenticateVoter(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login failed for email: {} - Error: {}", loginRequest.getEmail(), e.getMessage());
            throw e; // Let global exception handler deal with it
        }
    }
    
    /**
     * Get all candidates available for voting
     * Only authenticated voters can access this endpoint
     * 
     * @return List of CandidateDTO containing candidate information
     */
    @GetMapping("/candidates")
    public ResponseEntity<List<CandidateDTO>> getCandidates() {
        log.info("Voter requested candidate list");
        try {
            List<CandidateDTO> candidates = candidateService.getAllCandidates();
            return ResponseEntity.ok(candidates);
        } catch (Exception e) {
            log.error("Error retrieving candidates: {}", e.getMessage(), e);
            throw e; // Let global exception handler deal with it
        }
    }
    
    /**
     * Cast a vote for a candidate in an election
     * Only authenticated voters can access this endpoint
     * 
     * @param voteRequest The voting request containing candidate and election IDs
     * @param voterEmail The email of the authenticated voter (extracted from JWT)
     * @return ResponseEntity containing the created Vote
     */
    @PostMapping("/vote")
    public ResponseEntity<Vote> castVote(@RequestBody VoteRequestDTO voteRequest, 
                                       @RequestParam String voterEmail) {
        log.info("Voter {} attempting to cast vote for candidate {} in election {}", 
                voterEmail, voteRequest.getCandidateId(), voteRequest.getElectionId());
        try {
            Vote vote = votingService.castVote(voteRequest, voterEmail);
            return ResponseEntity.ok(vote);
        } catch (Exception e) {
            log.error("Voting failed for voter {}: {}", voterEmail, e.getMessage());
            throw e; // Let global exception handler deal with it
        }
    }
}
