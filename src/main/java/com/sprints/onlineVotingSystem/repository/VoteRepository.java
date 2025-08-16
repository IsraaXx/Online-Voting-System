package com.sprints.onlineVotingSystem.repository;

import com.sprints.onlineVotingSystem.domain.Election;
import com.sprints.onlineVotingSystem.domain.Voter;
import com.sprints.onlineVotingSystem.dto.CandidateResultDTO;
import com.sprints.onlineVotingSystem.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    
    @Query("SELECT new com.sprints.onlineVotingSystem.dto.CandidateResultDTO(c.name, COUNT(v)) " +
           "FROM Vote v " +
           "JOIN v.candidate c " +
           "GROUP BY c.id, c.name " +
           "ORDER BY COUNT(v) DESC")
    List<CandidateResultDTO> getCandidateVoteCounts();
    
    /**
     * Checks if a voter has already voted in a specific election
     */
    boolean existsByVoterAndElection(Voter voter, Election election);
}
