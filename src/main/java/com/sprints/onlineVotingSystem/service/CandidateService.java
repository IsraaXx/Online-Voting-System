package com.sprints.onlineVotingSystem.service;

import com.sprints.onlineVotingSystem.domain.Candidate;
import com.sprints.onlineVotingSystem.dto.CandidateDTO;
import com.sprints.onlineVotingSystem.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateService {
    
    private final CandidateRepository candidateRepository;
    
    /**
     * Retrieves all candidates as DTOs with their election information
     * 
     * @return List of CandidateDTO containing candidate information
     */
    public List<CandidateDTO> getAllCandidates() {
        log.info("Fetching all candidates");
        try {
            List<Candidate> candidates = StreamSupport.stream(candidateRepository.findAll().spliterator(), false)
                    .collect(Collectors.toList());
            List<CandidateDTO> candidateDTOs = candidates.stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
            
            log.info("Successfully retrieved {} candidates", candidateDTOs.size());
            return candidateDTOs;
        } catch (Exception e) {
            log.error("Error fetching candidates: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve candidates", e);
        }
    }
    
    /**
     * Retrieves all candidates as entities
     * 
     * @return List of Candidate entities
     */
    public List<Candidate> getAllCandidateEntities() {
        log.info("Fetching all candidate entities");
        try {
            List<Candidate> candidates = StreamSupport.stream(candidateRepository.findAll().spliterator(), false)
                    .collect(Collectors.toList());
            log.info("Successfully retrieved {} candidate entities", candidates.size());
            return candidates;
        } catch (Exception e) {
            log.error("Error fetching candidate entities: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve candidate entities", e);
        }
    }
    
    /**
     * Creates a new candidate
     * 
     * @param candidate The candidate to create
     * @return The created candidate with generated ID
     */
    public Candidate createCandidate(Candidate candidate) {
        log.info("Creating new candidate: {}", candidate.getName());
        
        // Validate candidate data
        if (candidate.getName() == null || candidate.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Candidate name is required");
        }
        
        if (candidate.getElection() == null || candidate.getElection().getId() == null) {
            throw new IllegalArgumentException("Election is required for candidate");
        }
        
        Candidate savedCandidate = candidateRepository.save(candidate);
        log.info("Candidate created successfully with ID: {}", savedCandidate.getId());
        
        return savedCandidate;
    }
    
    /**
     * Maps Candidate entity to CandidateDTO
     * 
     * @param candidate The candidate entity
     * @return CandidateDTO with mapped data
     */
    private CandidateDTO mapToDTO(Candidate candidate) {
        return new CandidateDTO(
                candidate.getId(),
                candidate.getName(),
                getPartyFromCandidate(candidate), // You can extend this based on your domain model
                candidate.getElection() != null ? candidate.getElection().getTitle() : "Unknown Election"
        );
    }
    
    /**
     * Extracts party information from candidate
     * This is a placeholder - you can extend the Candidate entity to include party field
     * 
     * @param candidate The candidate entity
     * @return Party name or "Independent" as default
     */
    private String getPartyFromCandidate(Candidate candidate) {
        // TODO: Extend Candidate entity to include party field
        // For now, returning a default value
        return "Independent";
    }
}
