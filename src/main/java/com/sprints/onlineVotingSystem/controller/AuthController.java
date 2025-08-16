package com.sprints.onlineVotingSystem.controller;

import com.sprints.onlineVotingSystem.dto.AdminRegistrationDTO;
import com.sprints.onlineVotingSystem.dto.LoginRequestDTO;
import com.sprints.onlineVotingSystem.dto.LoginResponseDTO;
import com.sprints.onlineVotingSystem.dto.VoterRegistrationDTO;
import com.sprints.onlineVotingSystem.domain.Voter;
import com.sprints.onlineVotingSystem.service.AuthService;
import com.sprints.onlineVotingSystem.service.VoterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final VoterService voterService;

    /**
     * Public endpoint for admin login
     * 
     * @param loginRequest The login credentials
     * @return LoginResponseDTO containing JWT token and user info
     */
    @PostMapping("/admin/login")
    public ResponseEntity<LoginResponseDTO> adminLogin(@Valid @RequestBody LoginRequestDTO loginRequest) {
        log.info("Admin login attempt for email: {}", loginRequest.getEmail());
        try {
            LoginResponseDTO response = authService.authenticateAdmin(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Admin login failed for email: {} - Error: {}", loginRequest.getEmail(), e.getMessage());
            throw e;
        }
    }

    /**
     * Public endpoint for voter login
     * 
     * @param loginRequest The login credentials
     * @return LoginResponseDTO containing JWT token and user info
     */
    @PostMapping("/voter/login")
    public ResponseEntity<LoginResponseDTO> voterLogin(@Valid @RequestBody LoginRequestDTO loginRequest) {
        log.info("Voter login attempt for email: {}", loginRequest.getEmail());
        try {
            LoginResponseDTO response = authService.authenticateVoter(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Voter login failed for email: {} - Error: {}", loginRequest.getEmail(), e.getMessage());
            throw e;
        }
    }

    /**
     * Public endpoint for voter registration
     * 
     * @param voterDTO The voter data to register
     * @return ResponseEntity containing the created voter
     */
    @PostMapping("/voter/register")
    public ResponseEntity<Voter> registerVoter(@Valid @RequestBody VoterRegistrationDTO voterDTO) {
        log.info("Public voter registration: {}", voterDTO.getEmail());
        try {
            Voter voter = Voter.builder()
                    .name(voterDTO.getName())
                    .email(voterDTO.getEmail())
                    .passwordHash(voterDTO.getPassword()) // Note: Should be hashed in service
                    .city(voterDTO.getCity())
                    .role(com.sprints.onlineVotingSystem.domain.Role.VOTER)
                    .build();
            Voter createdVoter = voterService.registerVoter(voter);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdVoter);
        } catch (Exception e) {
            log.error("Error registering voter: {}", e.getMessage(), e);
            throw e;
        }
    }
}
