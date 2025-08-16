package com.sprints.onlineVotingSystem.controller;

import com.sprints.onlineVotingSystem.domain.Candidate;
import com.sprints.onlineVotingSystem.domain.Election;
import com.sprints.onlineVotingSystem.domain.Voter;
import com.sprints.onlineVotingSystem.dto.CandidateDTO;
import com.sprints.onlineVotingSystem.dto.CandidateRegistrationDTO;
import com.sprints.onlineVotingSystem.dto.CandidateResultDTO;
import com.sprints.onlineVotingSystem.dto.ElectionDTO;
import com.sprints.onlineVotingSystem.dto.VoterRegistrationDTO;
import com.sprints.onlineVotingSystem.dto.AdminRegistrationDTO;
import com.sprints.onlineVotingSystem.service.CandidateService;
import com.sprints.onlineVotingSystem.service.ElectionResultService;
import com.sprints.onlineVotingSystem.service.ElectionService;
import com.sprints.onlineVotingSystem.service.VoterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    
    private final ElectionResultService electionResultService;
    private final ElectionService electionService;
    private final CandidateService candidateService;
    private final VoterService voterService;
    
    /**
     * POST endpoint to create a new election
     * Only admins can create elections
     * 
     * @param electionDTO The election data to create
     * @return ResponseEntity containing the created election
     */
    @PostMapping("/elections")
    public ResponseEntity<Election> createElection(@Valid @RequestBody ElectionDTO electionDTO) {
        log.info("Admin creating new election: {}", electionDTO.getTitle());
        try {
            Election election = Election.builder()
                    .title(electionDTO.getTitle())
                    .startDate(electionDTO.getStartDate())
                    .endDate(electionDTO.getEndDate())
                    .build();
            Election createdElection = electionService.createElection(election);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdElection);
        } catch (Exception e) {
            log.error("Error creating election: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * GET endpoint to retrieve all elections
     * Only admins can view all elections
     * 
     * @return ResponseEntity containing a list of all elections
     */
    @GetMapping("/elections")
    public ResponseEntity<List<Election>> getAllElections() {
        log.info("Admin requested all elections");
        try {
            List<Election> elections = electionService.getAllElections();
            return ResponseEntity.ok(elections);
        } catch (Exception e) {
            log.error("Error retrieving elections: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * POST endpoint to create a new candidate
     * Only admins can create candidates
     * 
     * @param candidateDTO The candidate data to create
     * @return ResponseEntity containing the created candidate
     */
    @PostMapping("/candidates")
    public ResponseEntity<Candidate> createCandidate(@Valid @RequestBody CandidateRegistrationDTO candidateDTO) {
        log.info("Admin creating new candidate: {}", candidateDTO.getName());
        try {
            Candidate candidate = new Candidate();
            candidate.setName(candidateDTO.getName());
            candidate.setElection(electionService.getElectionById(candidateDTO.getElectionId()));
            Candidate createdCandidate = candidateService.createCandidate(candidate);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCandidate);
        } catch (Exception e) {
            log.error("Error creating candidate: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * GET endpoint to retrieve all candidates
     * Only admins can view all candidates
     * 
     * @return ResponseEntity containing a list of all candidates
     */
    @GetMapping("/candidates")
    public ResponseEntity<List<Candidate>> getAllCandidates() {
        log.info("Admin requested all candidates");
        try {
            List<Candidate> candidates = candidateService.getAllCandidateEntities();
            return ResponseEntity.ok(candidates);
        } catch (Exception e) {
            log.error("Error retrieving candidates: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * POST endpoint to register a new voter
     * Only admins can register voters
     * 
     * @param voterDTO The voter data to register
     * @return ResponseEntity containing the created voter
     */
    @PostMapping("/voters")
    public ResponseEntity<Voter> registerVoter(@Valid @RequestBody VoterRegistrationDTO voterDTO) {
        log.info("Admin registering new voter: {}", voterDTO.getEmail());
        try {
            Voter voter = Voter.builder()
                    .name(voterDTO.getName())
                    .email(voterDTO.getEmail())
                    .passwordHash(voterDTO.getPassword()) // Note: Should be hashed in service
                    .city(voterDTO.getCity())
                    .role(com.sprints.onlineVotingSystem.domain.Role.VOTER)
                    .build();
            Voter createdVoter = voterService.registerVoter(voter);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdVoter);
        } catch (Exception e) {
            log.error("Error registering voter: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * POST endpoint to register a new admin
     * Only existing admins can register new admins
     * 
     * @param adminDTO The admin data to register
     * @return ResponseEntity containing the created admin
     */
    @PostMapping("/admins")
    public ResponseEntity<Voter> registerAdmin(@Valid @RequestBody AdminRegistrationDTO adminDTO) {
        log.info("Admin registering new admin: {}", adminDTO.getEmail());
        try {
            Voter admin = Voter.builder()
                    .name(adminDTO.getName())
                    .email(adminDTO.getEmail())
                    .passwordHash(adminDTO.getPassword()) // Note: Should be hashed in service
                    .city(adminDTO.getCity())
                    .role(com.sprints.onlineVotingSystem.domain.Role.ADMIN)
                    .build();
            Voter createdAdmin = voterService.registerVoter(admin);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAdmin);
        } catch (Exception e) {
            log.error("Error registering admin: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * PUT endpoint to assign a voter to a city
     * Only admins can assign voters
     * 
     * @param voterId The voter ID
     * @param city The city to assign
     * @return ResponseEntity indicating success
     */
    @PutMapping("/voters/{voterId}/assign")
    public ResponseEntity<String> assignVoterToCity(@PathVariable Long voterId, @RequestParam String city) {
        log.info("Admin assigning voter {} to city: {}", voterId, city);
        try {
            voterService.assignVoterToCity(voterId, city);
            return ResponseEntity.ok("Voter successfully assigned to city: " + city);
        } catch (Exception e) {
            log.error("Error assigning voter to city: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * GET endpoint to retrieve voters by city
     * Only admins can view voter assignments
     * 
     * @param city The city to filter by
     * @return ResponseEntity containing a list of voters in the city
     */
    @GetMapping("/voters/city/{city}")
    public ResponseEntity<List<Voter>> getVotersByCity(@PathVariable String city) {
        log.info("Admin requested voters for city: {}", city);
        try {
            List<Voter> voters = voterService.getVotersByCity(city);
            return ResponseEntity.ok(voters);
        } catch (Exception e) {
            log.error("Error retrieving voters by city: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * GET endpoint to retrieve election results
     * Returns the total number of votes per candidate, sorted by vote count in descending order
     * 
     * @return ResponseEntity containing a list of CandidateResultDTO
     */
    @GetMapping("/results")
    public ResponseEntity<List<CandidateResultDTO>> getElectionResults() {
        log.info("Admin requested election results");
        try {
            List<CandidateResultDTO> results = electionResultService.getElectionResults();
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("Error retrieving election results: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
