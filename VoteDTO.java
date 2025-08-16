package com.example.votingsystem.dto;

import jakarta.validation.constraints.NotNull;

public class VoteDTO {

    @NotNull(message = "Voter ID is required")
    private Long voterId;

    @NotNull(message = "Candidate ID is required")
    private Long candidateId;

    @NotNull(message = "Election ID is required")
    private Long electionId;

    // getters & setters
}
