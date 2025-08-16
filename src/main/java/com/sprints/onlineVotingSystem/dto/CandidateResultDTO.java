package com.sprints.onlineVotingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateResultDTO {
    private String candidateName;
    private Long totalVotes;
}
