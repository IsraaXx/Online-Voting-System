package com.sprints.onlineVotingSystem.service;

import com.sprints.onlineVotingSystem.domain.Role;
import com.sprints.onlineVotingSystem.domain.Voter;
import com.sprints.onlineVotingSystem.dto.LoginRequestDTO;
import com.sprints.onlineVotingSystem.dto.LoginResponseDTO;
import com.sprints.onlineVotingSystem.exception.BadRequestException;
import com.sprints.onlineVotingSystem.repository.VoterRepository;
import com.sprints.onlineVotingSystem.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final VoterRepository voterRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Authenticates a voter and returns a JWT token
     * 
     * @param loginRequest The login credentials
     * @return LoginResponseDTO containing the JWT token and user info
     * @throws BadRequestException if credentials are invalid
     */
    public LoginResponseDTO authenticateVoter(LoginRequestDTO loginRequest) {
        log.info("Attempting to authenticate voter with email: {}", loginRequest.getEmail());
        
        // Validate input
        if (loginRequest.getEmail() == null || loginRequest.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Email is required");
        }
        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            throw new BadRequestException("Password is required");
        }
        
        // Find voter by email
        Voter voter = voterRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));
        
        // Verify password
        if (!passwordEncoder.matches(loginRequest.getPassword(), voter.getPasswordHash())) {
            log.warn("Failed login attempt for email: {}", loginRequest.getEmail());
            throw new BadRequestException("Invalid email or password");
        }
        
        // Verify role is VOTER
        if (voter.getRole() != Role.VOTER) {
            log.warn("Access denied for non-voter user: {} with role: {}", loginRequest.getEmail(), voter.getRole());
            throw new BadRequestException("Access denied. Voter role required.");
        }
        
        // Generate JWT token
        String token = jwtUtil.generateToken(voter.getEmail(), voter.getRole().name());
        
        log.info("Successful authentication for voter: {}", voter.getEmail());
        
        return new LoginResponseDTO(
                token,
                voter.getEmail(),
                voter.getRole().name(),
                "Login successful"
        );
    }
}
