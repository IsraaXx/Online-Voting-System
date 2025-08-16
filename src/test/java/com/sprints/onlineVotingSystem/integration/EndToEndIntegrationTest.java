package com.sprints.onlineVotingSystem.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprints.onlineVotingSystem.domain.Candidate;
import com.sprints.onlineVotingSystem.domain.Election;
import com.sprints.onlineVotingSystem.domain.Role;
import com.sprints.onlineVotingSystem.domain.Voter;
import com.sprints.onlineVotingSystem.dto.LoginRequestDTO;
import com.sprints.onlineVotingSystem.dto.VoteRequestDTO;
import com.sprints.onlineVotingSystem.repository.CandidateRepository;
import com.sprints.onlineVotingSystem.repository.ElectionRepository;
import com.sprints.onlineVotingSystem.repository.VoterRepository;
import com.sprints.onlineVotingSystem.repository.VoteRepository;
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

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class EndToEndIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private VoterRepository voterRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Election testElection;
    private Candidate testCandidate1;
    private Candidate testCandidate2;
    private Voter testVoter;
    private String adminToken;
    private String voterToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();

        // Create test data
        setupTestData();
    }

    private void setupTestData() {
        // Create test election
        testElection = Election.builder()
                .title("Test Election 2024")
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(1))
                .build();
        testElection = electionRepository.save(testElection);

        // Create test candidates
        testCandidate1 = new Candidate();
        testCandidate1.setName("John Doe");
        testCandidate1.setElection(testElection);
        testCandidate1 = candidateRepository.save(testCandidate1);

        testCandidate2 = new Candidate();
        testCandidate2.setName("Jane Smith");
        testCandidate2.setElection(testElection);
        testCandidate2 = candidateRepository.save(testCandidate2);

        // Create test voter
        testVoter = new Voter();
        testVoter.setEmail("voter@test.com");
        testVoter.setPasswordHash(passwordEncoder.encode("password123"));
        testVoter.setRole(Role.VOTER);
        testVoter.setName("Test Voter");
        testVoter.setCity("Test City");
        testVoter = voterRepository.save(testVoter);

        // Generate tokens
        adminToken = jwtUtil.generateToken("admin@test.com", "ADMIN");
        voterToken = jwtUtil.generateToken("voter@test.com", "VOTER");
    }

    @Test
    void testCompleteVotingFlow() throws Exception {
        // 1. Admin creates election (simulated by setup)
        assert testElection.getId() != null;
        assert testCandidate1.getId() != null;
        assert testCandidate2.getId() != null;

        // 2. Voter logs in and gets JWT
        LoginRequestDTO loginRequest = new LoginRequestDTO("voter@test.com", "password123");
        
        mockMvc.perform(post("/api/voters/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.role").value("VOTER"));

        // 3. Voter gets candidates list
        mockMvc.perform(get("/api/voters/candidates")
                .header("Authorization", "Bearer " + voterToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].name").value("Jane Smith"));

        // 4. Voter casts a vote
        VoteRequestDTO voteRequest = new VoteRequestDTO();
        voteRequest.setCandidateId(testCandidate1.getId());
        voteRequest.setElectionId(testElection.getId());

        mockMvc.perform(post("/api/voters/vote?voterEmail=voter@test.com")
                .header("Authorization", "Bearer " + voterToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isOk());

        // 5. Admin retrieves results
        mockMvc.perform(get("/admin/results")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].candidateName").value("John Doe"))
                .andExpect(jsonPath("$[0].voteCount").value(1))
                .andExpect(jsonPath("$[1].candidateName").value("Jane Smith"))
                .andExpect(jsonPath("$[1].voteCount").value(0));

        // Verify vote was actually recorded
        assert voteRepository.count() == 1;
    }

    @Test
    void testUnauthorizedAccess() throws Exception {
        // Test accessing admin endpoints without proper role
        mockMvc.perform(get("/admin/results")
                .header("Authorization", "Bearer " + voterToken))
                .andExpect(status().isForbidden());

        // Test accessing voter endpoints without authentication
        mockMvc.perform(get("/api/voters/candidates"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testInvalidToken() throws Exception {
        // Test with invalid token
        mockMvc.perform(get("/api/voters/candidates")
                .header("Authorization", "Bearer invalid_token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testVotingRestrictions() throws Exception {
        // Create a closed election
        Election closedElection = Election.builder()
                .title("Closed Election")
                .startDate(LocalDate.now().minusDays(10))
                .endDate(LocalDate.now().minusDays(5))
                .build();
        closedElection = electionRepository.save(closedElection);

        Candidate closedCandidate = new Candidate();
        closedCandidate.setName("Closed Candidate");
        closedCandidate.setElection(closedElection);
        closedCandidate = candidateRepository.save(closedCandidate);

        VoteRequestDTO voteRequest = new VoteRequestDTO();
        voteRequest.setCandidateId(closedCandidate.getId());
        voteRequest.setElectionId(closedElection.getId());

        // Try to vote in closed election
        mockMvc.perform(post("/api/voters/vote?voterEmail=voter@test.com")
                .header("Authorization", "Bearer " + voterToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isBadRequest());
    }
}
