package com.sprints.onlineVotingSystem.repository;

import com.sprints.onlineVotingSystem.domain.Candidate;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends CrudRepository<Candidate, Long> {
    
    List<Candidate> findByElectionId(Long electionId);
    
    @Query("SELECT c FROM Candidate c WHERE c.name LIKE %:name%")
    List<Candidate> findByCandidateNameContaining(@Param("name") String name);
    
    @Modifying
    @Query("UPDATE Candidate c SET c.name = :name WHERE c.id = :id")
    int updateCandidateName(@Param("id") Long id, @Param("name") String name);
    
    @Modifying
    @Query("DELETE FROM Candidate c WHERE c.election.id = :electionId")
    int deleteByElectionId(@Param("electionId") Long electionId);
}
