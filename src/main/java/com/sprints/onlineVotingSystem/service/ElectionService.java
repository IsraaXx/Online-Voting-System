package com.sprints.onlineVotingSystem.service;

import com.sprints.onlineVotingSystem.domain.Election;
import com.sprints.onlineVotingSystem.exception.ResourceNotFoundException;
import com.sprints.onlineVotingSystem.repository.ElectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElectionService {
    
    private final ElectionRepository electionRepository;
    
    /**
     * Creates a new election
     * 
     * @param election The election to create
     * @return The created election with generated ID
     */
    public Election createElection(Election election) {
        log.info("Creating new election: {}", election.getTitle());
        
        // Validate election dates
        if (election.getStartDate() == null || election.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date are required");
        }
        
        if (election.getStartDate().isAfter(election.getEndDate())) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        
        Election savedElection = electionRepository.save(election);
        log.info("Election created successfully with ID: {}", savedElection.getId());
        
        return savedElection;
    }
    
    /**
     * Retrieves all elections
     * 
     * @return List of all elections
     */
    public List<Election> getAllElections() {
        log.info("Retrieving all elections");
        return electionRepository.findAll();
    }
    
    /**
     * Retrieves an election by ID
     * 
     * @param id The election ID
     * @return The election if found
     * @throws ResourceNotFoundException if election not found
     */
    public Election getElectionById(Long id) {
        log.info("Retrieving election with ID: {}", id);
        return electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with ID: " + id));
    }
    
    /**
     * Checks if an election is currently active (within voting window)
     * 
     * @param electionId The election ID
     * @return true if election is active, false otherwise
     */
    public boolean isElectionActive(Long electionId) {
        Election election = getElectionById(electionId);
        java.time.LocalDate today = java.time.LocalDate.now();
        
        return !today.isBefore(election.getStartDate()) && !today.isAfter(election.getEndDate());
    }
}
