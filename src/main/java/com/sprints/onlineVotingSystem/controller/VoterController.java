package com.sprints.onlineVotingSystem.controller;

import com.sprints.onlineVotingSystem.domain.Voter;
import com.sprints.onlineVotingSystem.dto.CandidateDTO;
import com.sprints.onlineVotingSystem.dto.LoginRequestDTO;
import com.sprints.onlineVotingSystem.dto.LoginResponseDTO;
import com.sprints.onlineVotingSystem.service.AuthService;
import com.sprints.onlineVotingSystem.service.CandidateService;
import com.sprints.onlineVotingSystem.service.VoterService;
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
}
