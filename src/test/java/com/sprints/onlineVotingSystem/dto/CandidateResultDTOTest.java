package com.sprints.onlineVotingSystem.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CandidateResultDTOTest {

    @Test
    void testCandidateResultDTO_ConstructorAndGetters() {
        // Arrange & Act
        CandidateResultDTO dto = new CandidateResultDTO("Test Candidate", 100L);

        // Assert
        assertEquals("Test Candidate", dto.getCandidateName());
        assertEquals(100L, dto.getTotalVotes());
    }

    @Test
    void testCandidateResultDTO_NoArgsConstructor() {
        // Arrange & Act
        CandidateResultDTO dto = new CandidateResultDTO();

        // Assert
        assertNull(dto.getCandidateName());
        assertNull(dto.getTotalVotes());
    }

    @Test
    void testCandidateResultDTO_Setters() {
        // Arrange
        CandidateResultDTO dto = new CandidateResultDTO();

        // Act
        dto.setCandidateName("New Candidate");
        dto.setTotalVotes(200L);

        // Assert
        assertEquals("New Candidate", dto.getCandidateName());
        assertEquals(200L, dto.getTotalVotes());
    }

    @Test
    void testCandidateResultDTO_EqualsAndHashCode() {
        // Arrange
        CandidateResultDTO dto1 = new CandidateResultDTO("Candidate A", 100L);
        CandidateResultDTO dto2 = new CandidateResultDTO("Candidate A", 100L);
        CandidateResultDTO dto3 = new CandidateResultDTO("Candidate B", 100L);

        // Assert
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testCandidateResultDTO_ToString() {
        // Arrange
        CandidateResultDTO dto = new CandidateResultDTO("Test Candidate", 100L);

        // Act
        String toString = dto.toString();

        // Assert
        assertTrue(toString.contains("Test Candidate"));
        assertTrue(toString.contains("100"));
    }
}
