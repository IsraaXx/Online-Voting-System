package com.example.votingsystem.service;

import com.example.votingsystem.dto.VoteDTO;
import com.example.votingsystem.entity.Candidate;
import com.example.votingsystem.entity.Election;
import com.example.votingsystem.entity.Vote;
import com.example.votingsystem.entity.Voter;
import com.example.votingsystem.exception.AlreadyVotedException;
import com.example.votingsystem.repository.CandidateRepository;
import com.example.votingsystem.repository.ElectionRepository;
import com.example.votingsystem.repository.VoteRepository;
import com.example.votingsystem.repository.VoterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final VoterRepository voterRepository;
    private final CandidateRepository candidateRepository;
    private final ElectionRepository electionRepository;

    public VoteService(VoteRepository voteRepository,
                       VoterRepository voterRepository,
                       CandidateRepository candidateRepository,
                       ElectionRepository electionRepository) {
        this.voteRepository = voteRepository;
        this.voterRepository = voterRepository;
        this.candidateRepository = candidateRepository;
        this.electionRepository = electionRepository;
    }

    @Transactional
    public void castVote(VoteDTO voteDTO) {
        Voter voter = voterRepository.findById(voteDTO.getVoterId())
                .orElseThrow(() -> new RuntimeException("Voter not found"));

        if (voteRepository.existsByVoterIdAndElectionId(voteDTO.getVoterId(), voteDTO.getElectionId())) {
            throw new AlreadyVotedException("Voter has already cast a vote in this election");
        }

        Candidate candidate = candidateRepository.findById(voteDTO.getCandidateId())
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        Election election = electionRepository.findById(voteDTO.getElectionId())
                .orElseThrow(() -> new RuntimeException("Election not found"));

        Vote vote = new Vote();
        vote.setVoter(voter);
        vote.setCandidate(candidate);
        vote.setElection(election);

        voteRepository.save(vote);
    }
}
