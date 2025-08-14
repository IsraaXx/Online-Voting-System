package com.sprints.onlineVotingSystem.repository;

import com.sprints.onlineVotingSystem.domain.Voter;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface VoterRepository extends CrudRepository<Voter, Long> {
    Optional<Voter> findByEmail(String email);
    boolean existsByEmail(String email);

}
