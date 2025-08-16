package com.sprints.onlineVotingSystem.service;

import com.sprints.onlineVotingSystem.domain.Voter;
import com.sprints.onlineVotingSystem.exception.BadRequestException;
import com.sprints.onlineVotingSystem.exception.ResourceNotFoundException;
import com.sprints.onlineVotingSystem.repository.VoterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VoterService {

    private final VoterRepository voterRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public VoterService(VoterRepository voterRepository, PasswordEncoder passwordEncoder) {
        this.voterRepository = voterRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new voter
     * @param voter the voter to register
     * @return the registered voter
     */
    @Transactional
    public Voter registerVoter(Voter voter) {
        if (voterRepository.existsByEmail(voter.getEmail())) {
            throw new BadRequestException("Voter with this email already exists");
        }
        
        // Hash the password
        voter.setPasswordHash(passwordEncoder.encode(voter.getPasswordHash()));
        
        return voterRepository.save(voter);
    }

    /**
     * Get all voters filtered by city
     * @param city the city to filter by
     * @return list of voters in the specified city
     */
    public List<Voter> getVotersByCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new BadRequestException("City name cannot be null or empty");
        }
        return voterRepository.findByCity(city);
    }
    
    /**
     * Get voter by ID
     * @param id the voter ID
     * @return the voter if found
     * @throws ResourceNotFoundException if voter not found
     */
    public Voter getVoterById(Long id) {
        return voterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voter", "id", id));
    }
    
    /**
     * Assign a voter to a city
     * @param voterId the voter ID
     * @param city the city to assign
     * @throws ResourceNotFoundException if voter not found
     */
    @Transactional
    public void assignVoterToCity(Long voterId, String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new BadRequestException("City name cannot be null or empty");
        }
        
        Voter voter = getVoterById(voterId);
        int updatedRows = voterRepository.updateVoterCity(voterId, city);
        
        if (updatedRows == 0) {
            throw new BadRequestException("Failed to update voter city");
        }
    }
}
