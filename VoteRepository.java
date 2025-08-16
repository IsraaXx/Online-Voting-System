package com.example.votingsystem.repository;

import com.example.votingsystem.entity.Vote;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends CrudRepository<Vote, Long> {
    boolean existsByVoterIdAndElectionId(Long voterId, Long electionId);
}
