package com.sprints.onlineVotingSystem.service;

import com.sprints.onlineVotingSystem.domain.Candidate;
import com.sprints.onlineVotingSystem.dto.CandidateDTO;
import com.sprints.onlineVotingSystem.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateService {
    
    private final CandidateRepository candidateRepository;
    
    /**
     * Retrieves all candidates with their election information
     * 
     * @return List of CandidateDTO containing candidate information
     */
    public List<CandidateDTO> getAllCandidates() {
        log.info("Fetching all candidates");
        try {
            List<Candidate> candidates = candidateRepository.findAllWithElection();
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
     * This is a placeholder - you can extend the Candidate entity to include party information
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
