package com.sprints.onlineVotingSystem.service;

import com.sprints.onlineVotingSystem.domain.Role;
import com.sprints.onlineVotingSystem.domain.Voter;
import com.sprints.onlineVotingSystem.dto.LoginRequestDTO;
import com.sprints.onlineVotingSystem.dto.LoginResponseDTO;
import com.sprints.onlineVotingSystem.exception.BadRequestException;
import com.sprints.onlineVotingSystem.repository.VoterRepository;
import com.sprints.onlineVotingSystem.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private VoterRepository voterRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private LoginRequestDTO validLoginRequest;
    private Voter validVoter;

    @BeforeEach
    void setUp() {
        validLoginRequest = new LoginRequestDTO("test@example.com", "password123");
        validVoter = Voter.builder()
                .id(1L)
                .email("test@example.com")
                .passwordHash("hashedPassword")
                .role(Role.VOTER)
                .name("Test Voter")
                .city("Test City")
                .build();
    }

    @Test
    void authenticateVoter_Success() {
        // Arrange
        when(voterRepository.findByEmail(validLoginRequest.getEmail())).thenReturn(Optional.of(validVoter));
        when(passwordEncoder.matches(validLoginRequest.getPassword(), validVoter.getPasswordHash())).thenReturn(true);
        when(jwtUtil.generateToken(validVoter.getEmail(), validVoter.getRole().name())).thenReturn("jwt.token.here");

        // Act
        LoginResponseDTO response = authService.authenticateVoter(validLoginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwt.token.here", response.getToken());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("VOTER", response.getRole());
        assertEquals("Login successful", response.getMessage());
        
        verify(voterRepository).findByEmail(validLoginRequest.getEmail());
        verify(passwordEncoder).matches(validLoginRequest.getPassword(), validVoter.getPasswordHash());
        verify(jwtUtil).generateToken(validVoter.getEmail(), validVoter.getRole().name());
    }

    @Test
    void authenticateVoter_EmptyEmail_ThrowsBadRequestException() {
        // Arrange
        LoginRequestDTO invalidRequest = new LoginRequestDTO("", "password123");

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            authService.authenticateVoter(invalidRequest);
        });
        
        verify(voterRepository, never()).findByEmail(any());
    }

    @Test
    void authenticateVoter_EmptyPassword_ThrowsBadRequestException() {
        // Arrange
        LoginRequestDTO invalidRequest = new LoginRequestDTO("test@example.com", "");

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            authService.authenticateVoter(invalidRequest);
        });
        
        verify(voterRepository, never()).findByEmail(any());
    }

    @Test
    void authenticateVoter_InvalidEmail_ThrowsBadRequestException() {
        // Arrange
        when(voterRepository.findByEmail(validLoginRequest.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            authService.authenticateVoter(validLoginRequest);
        });
        
        verify(voterRepository).findByEmail(validLoginRequest.getEmail());
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void authenticateVoter_InvalidPassword_ThrowsBadRequestException() {
        // Arrange
        when(voterRepository.findByEmail(validLoginRequest.getEmail())).thenReturn(Optional.of(validVoter));
        when(passwordEncoder.matches(validLoginRequest.getPassword(), validVoter.getPasswordHash())).thenReturn(false);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            authService.authenticateVoter(validLoginRequest);
        });
        
        verify(voterRepository).findByEmail(validLoginRequest.getEmail());
        verify(passwordEncoder).matches(validLoginRequest.getPassword(), validVoter.getPasswordHash());
        verify(jwtUtil, never()).generateToken(any(), any());
    }

    @Test
    void authenticateVoter_NonVoterRole_ThrowsBadRequestException() {
        // Arrange
        Voter adminVoter = Voter.builder()
                .id(1L)
                .email("admin@example.com")
                .passwordHash("hashedPassword")
                .role(Role.ADMIN)
                .name("Admin User")
                .city("Test City")
                .build();
        
        when(voterRepository.findByEmail(validLoginRequest.getEmail())).thenReturn(Optional.of(adminVoter));
        when(passwordEncoder.matches(validLoginRequest.getPassword(), adminVoter.getPasswordHash())).thenReturn(true);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            authService.authenticateVoter(validLoginRequest);
        });
        
        verify(voterRepository).findByEmail(validLoginRequest.getEmail());
        verify(passwordEncoder).matches(validLoginRequest.getPassword(), adminVoter.getPasswordHash());
        verify(jwtUtil, never()).generateToken(any(), any());
    }
}
