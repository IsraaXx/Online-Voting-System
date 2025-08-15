package com.sprints.onlineVotingSystem.controller;

import com.sprints.onlineVotingSystem.domain.Voter;
import com.sprints.onlineVotingSystem.service.VoterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/voters")
public class VoterController {

    private final VoterService voterService;

    @Autowired
    public VoterController(VoterService voterService) {
        this.voterService = voterService;
    }

    /**
     * Get all voters filtered by city
     * @param cityName the city name to filter by
     * @return list of voters in the specified city
     */
    @GetMapping("/city/{cityName}")
    public ResponseEntity<List<Voter>> getVotersByCity(@PathVariable String cityName) {
        List<Voter> voters = voterService.getVotersByCity(cityName);
        return ResponseEntity.ok(voters);
    }
}
