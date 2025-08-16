package com.example.votingsystem.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ElectionDTO {

    @NotBlank(message = "Election name is required")
    private String name;

    @NotNull(message = "Start time is required")
    @FutureOrPresent(message = "Start time must be now or in the future")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    private LocalDateTime endTime;

    // getters & setters
}
