package com.sprints.onlineVotingSystem.service;

import com.sprints.onlineVotingSystem.domain.Voter;
import com.sprints.onlineVotingSystem.repository.VoterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoterService {

    private final VoterRepository voterRepository;

    @Autowired
    public VoterService(VoterRepository voterRepository) {
        this.voterRepository = voterRepository;
    }

    /**
     * Get all voters filtered by city
     * @param city the city to filter by
     * @return list of voters in the specified city
     */
    public List<Voter> getVotersByCity(String city) {
        return voterRepository.findByCity(city);
    }
}
