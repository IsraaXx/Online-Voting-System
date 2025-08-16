package com.example.votingsystem.controller;

import com.example.votingsystem.dto.VoteDTO;
import com.example.votingsystem.service.VoteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/voter")
public class VoteController {

    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping("/vote")
    public ResponseEntity<String> castVote(@Valid @RequestBody VoteDTO voteDTO) {
        voteService.castVote(voteDTO);
        return ResponseEntity.ok("Vote cast successfully!");
    }
}
