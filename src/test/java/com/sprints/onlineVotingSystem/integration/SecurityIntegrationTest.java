package com.sprints.onlineVotingSystem.integration;

import com.sprints.onlineVotingSystem.domain.Role;
import com.sprints.onlineVotingSystem.domain.Voter;
import com.sprints.onlineVotingSystem.repository.VoterRepository;
import com.sprints.onlineVotingSystem.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class SecurityIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private VoterRepository voterRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private MockMvc mockMvc;
    private String validVoterToken;
    private String validAdminToken;
    private String expiredToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        setupTestData();
    }

    private void setupTestData() {
        // Create test voter
        Voter voter = new Voter();
        voter.setEmail("testvoter@example.com");
        voter.setPasswordHash(passwordEncoder.encode("password123"));
        voter.setRole(Role.VOTER);
        voter.setName("Test Voter");
        voter.setCity("Test City");
        voterRepository.save(voter);

        // Generate valid tokens
        validVoterToken = jwtUtil.generateToken("testvoter@example.com", "VOTER");
        validAdminToken = jwtUtil.generateToken("admin@example.com", "ADMIN");
        
        // Generate expired token (simulate by creating token with past expiration)
        // Note: This is a simplified approach - in real scenarios you'd need to mock time
        expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0Iiwicm9sZSI6IlZPVEVSIiwiZXhwIjoxNjQwOTk1MjAwfQ.invalid_signature";
    }

    @Test
    void testValidVoterTokenAccess() throws Exception {
        // Test that valid voter token can access voter endpoints
        mockMvc.perform(get("/api/voters/candidates")
                .header("Authorization", "Bearer " + validVoterToken))
                .andExpect(status().isOk());
    }

    @Test
    void testValidAdminTokenAccess() throws Exception {
        // Test that valid admin token can access admin endpoints
        mockMvc.perform(get("/admin/results")
                .header("Authorization", "Bearer " + validAdminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testVoterCannotAccessAdminEndpoints() throws Exception {
        // Test that voter token cannot access admin endpoints
        mockMvc.perform(get("/admin/results")
                .header("Authorization", "Bearer " + validVoterToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAdminCannotAccessVoterEndpoints() throws Exception {
        // Test that admin token cannot access voter endpoints
        mockMvc.perform(get("/api/voters/candidates")
                .header("Authorization", "Bearer " + validAdminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testMissingTokenReturnsUnauthorized() throws Exception {
        // Test that missing token returns unauthorized
        mockMvc.perform(get("/api/voters/candidates"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testInvalidTokenFormatReturnsUnauthorized() throws Exception {
        // Test that invalid token format returns unauthorized
        mockMvc.perform(get("/api/voters/candidates")
                .header("Authorization", "InvalidFormat token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testMalformedTokenReturnsUnauthorized() throws Exception {
        // Test that malformed token returns unauthorized
        mockMvc.perform(get("/api/voters/candidates")
                .header("Authorization", "Bearer malformed.token.here"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testPublicEndpointAccessibleWithoutToken() throws Exception {
        // Test that public endpoints are accessible without token
        mockMvc.perform(post("/api/voters/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testTokenWithInvalidRole() throws Exception {
        // Test token with invalid role
        String invalidRoleToken = jwtUtil.generateToken("test@example.com", "INVALID_ROLE");
        
        mockMvc.perform(get("/api/voters/candidates")
                .header("Authorization", "Bearer " + invalidRoleToken))
                .andExpect(status().isForbidden());
    }
}
