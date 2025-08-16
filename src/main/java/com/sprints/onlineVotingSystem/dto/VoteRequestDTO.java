package com.sprints.onlineVotingSystem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteRequestDTO {
    @NotNull(message = "Candidate ID is required")
    private Long candidateId;
    
    @NotNull(message = "Election ID is required")
    private Long electionId;
}
