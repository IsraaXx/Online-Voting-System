package com.sprints.onlineVotingSystem.controller;

import com.sprints.onlineVotingSystem.dto.CandidateResultDTO;
import com.sprints.onlineVotingSystem.service.ElectionResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    
    private final ElectionResultService electionResultService;
    
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
