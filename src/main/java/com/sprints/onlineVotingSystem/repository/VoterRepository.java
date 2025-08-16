package com.sprints.onlineVotingSystem.repository;

import com.sprints.onlineVotingSystem.domain.Voter;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoterRepository extends CrudRepository<Voter, Long> {
    Optional<Voter> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Voter> findByCity(String city);
    
    @Modifying
    @Query("UPDATE Voter v SET v.city = :city WHERE v.id = :id")
    int updateVoterCity(@Param("id") Long id, @Param("city") String city);
    
    @Modifying
    @Query("DELETE FROM Voter v WHERE v.email = :email")
    int deleteByEmail(@Param("email") String email);
    
    @Query("SELECT v FROM Voter v WHERE v.city = :city AND v.role = 'VOTER'")
    List<Voter> findEligibleVotersByCity(@Param("city") String city);
}
