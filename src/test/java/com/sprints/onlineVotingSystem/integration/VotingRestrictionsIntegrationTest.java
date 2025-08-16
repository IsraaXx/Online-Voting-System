package com.sprints.onlineVotingSystem.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprints.onlineVotingSystem.domain.Candidate;
import com.sprints.onlineVotingSystem.domain.Election;
import com.sprints.onlineVotingSystem.domain.Role;
import com.sprints.onlineVotingSystem.domain.Voter;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class VotingRestrictionsIntegrationTest {

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

    private Election activeElection;
    private Election futureElection;
    private Election pastElection;
    private Candidate candidate1;
    private Candidate candidate2;
    private Voter voter;
    private String voterToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        setupTestData();
    }

    private void setupTestData() {
        // Create elections with different time windows
        activeElection = Election.builder()
                .title("Active Election")
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(1))
                .build();
        activeElection = electionRepository.save(activeElection);

        futureElection = Election.builder()
                .title("Future Election")
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(10))
                .build();
        futureElection = electionRepository.save(futureElection);

        pastElection = Election.builder()
                .title("Past Election")
                .startDate(LocalDate.now().minusDays(10))
                .endDate(LocalDate.now().minusDays(1))
                .build();
        pastElection = electionRepository.save(pastElection);

        // Create candidates
        candidate1 = new Candidate();
        candidate1.setName("Candidate 1");
        candidate1.setElection(activeElection);
        candidate1 = candidateRepository.save(candidate1);

        candidate2 = new Candidate();
        candidate2.setName("Candidate 2");
        candidate2.setElection(activeElection);
        candidate2 = candidateRepository.save(candidate2);

        // Create voter
        voter = new Voter();
        voter.setEmail("voter@test.com");
        voter.setPasswordHash(passwordEncoder.encode("password123"));
        voter.setRole(Role.VOTER);
        voter.setName("Test Voter");
        voter.setCity("Test City");
        voter = voterRepository.save(voter);

        // Generate token
        voterToken = jwtUtil.generateToken("voter@test.com", "VOTER");
    }

    @Test
    void testVotingInActiveElection() throws Exception {
        // Test that voting is allowed in active election
        VoteRequestDTO voteRequest = new VoteRequestDTO();
        voteRequest.setCandidateId(candidate1.getId());
        voteRequest.setElectionId(activeElection.getId());

        mockMvc.perform(post("/api/voters/vote?voterEmail=voter@test.com")
                .header("Authorization", "Bearer " + voterToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isOk());

        // Verify vote was recorded
        assert voteRepository.count() == 1;
    }

    @Test
    void testVotingInFutureElection() throws Exception {
        // Test that voting is not allowed in future election
        VoteRequestDTO voteRequest = new VoteRequestDTO();
        voteRequest.setCandidateId(candidate1.getId());
        voteRequest.setElectionId(futureElection.getId());

        mockMvc.perform(post("/api/voters/vote?voterEmail=voter@test.com")
                .header("Authorization", "Bearer " + voterToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testVotingInPastElection() throws Exception {
        // Test that voting is not allowed in past election
        VoteRequestDTO voteRequest = new VoteRequestDTO();
        voteRequest.setCandidateId(candidate1.getId());
        voteRequest.setElectionId(pastElection.getId());

        mockMvc.perform(post("/api/voters/vote?voterEmail=voter@test.com")
                .header("Authorization", "Bearer " + voterToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testVotingForInvalidCandidate() throws Exception {
        // Test voting for candidate that doesn't exist
        VoteRequestDTO voteRequest = new VoteRequestDTO();
        voteRequest.setCandidateId(999L); // Non-existent candidate ID
        voteRequest.setElectionId(activeElection.getId());

        mockMvc.perform(post("/api/voters/vote?voterEmail=voter@test.com")
                .header("Authorization", "Bearer " + voterToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testVotingForCandidateInDifferentElection() throws Exception {
        // Create candidate in different election
        Candidate otherElectionCandidate = new Candidate();
        otherElectionCandidate.setName("Other Election Candidate");
        otherElectionCandidate.setElection(futureElection);
        otherElectionCandidate = candidateRepository.save(otherElectionCandidate);

        // Try to vote for candidate in different election
        VoteRequestDTO voteRequest = new VoteRequestDTO();
        voteRequest.setCandidateId(otherElectionCandidate.getId());
        voteRequest.setElectionId(activeElection.getId());

        mockMvc.perform(post("/api/voters/vote?voterEmail=voter@test.com")
                .header("Authorization", "Bearer " + voterToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDuplicateVoting() throws Exception {
        // Cast first vote
        VoteRequestDTO voteRequest = new VoteRequestDTO();
        voteRequest.setCandidateId(candidate1.getId());
        voteRequest.setElectionId(activeElection.getId());

        mockMvc.perform(post("/api/voters/vote?voterEmail=voter@test.com")
                .header("Authorization", "Bearer " + voterToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isOk());

        // Try to cast second vote in same election
        VoteRequestDTO secondVoteRequest = new VoteRequestDTO();
        secondVoteRequest.setCandidateId(candidate2.getId());
        secondVoteRequest.setElectionId(activeElection.getId());

        mockMvc.perform(post("/api/voters/vote?voterEmail=voter@test.com")
                .header("Authorization", "Bearer " + voterToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondVoteRequest)))
                .andExpect(status().isBadRequest());

        // Verify only one vote was recorded
        assert voteRepository.count() == 1;
    }

    @Test
    void testVotingWithInvalidElectionId() throws Exception {
        // Test voting with non-existent election ID
        VoteRequestDTO voteRequest = new VoteRequestDTO();
        voteRequest.setCandidateId(candidate1.getId());
        voteRequest.setElectionId(999L); // Non-existent election ID

        mockMvc.perform(post("/api/voters/vote?voterEmail=voter@test.com")
                .header("Authorization", "Bearer " + voterToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testVotingWithMissingVoterEmail() throws Exception {
        // Test voting without voter email parameter
        VoteRequestDTO voteRequest = new VoteRequestDTO();
        voteRequest.setCandidateId(candidate1.getId());
        voteRequest.setElectionId(activeElection.getId());

        mockMvc.perform(post("/api/voters/vote")
                .header("Authorization", "Bearer " + voterToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isBadRequest());
    }
}
