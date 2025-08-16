package com.example.votingsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CandidateDTO {

    @NotBlank(message = "Candidate name is required")
    @Size(min = 2, max = 50, message = "Candidate name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Party name is required")
    private String party;

    @NotBlank(message = "Description is required")
    private String description;

    // getters & setters
}
