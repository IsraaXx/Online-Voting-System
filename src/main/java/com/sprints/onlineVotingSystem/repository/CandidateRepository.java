package com.sprints.onlineVotingSystem.repository;

import com.sprints.onlineVotingSystem.domain.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    
    @Query("SELECT c FROM Candidate c JOIN FETCH c.election")
    List<Candidate> findAllWithElection();
}
