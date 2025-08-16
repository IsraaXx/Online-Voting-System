package com.sprints.onlineVotingSystem.service;

import com.sprints.onlineVotingSystem.dto.CandidateResultDTO;
import com.sprints.onlineVotingSystem.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElectionResultService {
    
    private final VoteRepository voteRepository;
    
    /**
     * Retrieves election results with vote counts per candidate, sorted by vote count in descending order
     * 
     * @return List of CandidateResultDTO containing candidate names and their vote counts
     */
    public List<CandidateResultDTO> getElectionResults() {
        log.info("Fetching election results");
        try {
            List<CandidateResultDTO> results = voteRepository.getCandidateVoteCounts();
            log.info("Successfully retrieved {} candidate results", results.size());
            return results;
        } catch (Exception e) {
            log.error("Error fetching election results: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve election results", e);
        }
    }
}
