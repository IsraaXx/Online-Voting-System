package com.sprints.onlineVotingSystem.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VoteRequestDTOTest {

    @Test
    void testVoteRequestDTO_ConstructorAndGetters() {
        // Arrange & Act
        VoteRequestDTO dto = new VoteRequestDTO(1L, 2L);

        // Assert
        assertEquals(1L, dto.getCandidateId());
        assertEquals(2L, dto.getElectionId());
    }

    @Test
    void testVoteRequestDTO_NoArgsConstructor() {
        // Arrange & Act
        VoteRequestDTO dto = new VoteRequestDTO();

        // Assert
        assertNull(dto.getCandidateId());
        assertNull(dto.getElectionId());
    }

    @Test
    void testVoteRequestDTO_Setters() {
        // Arrange
        VoteRequestDTO dto = new VoteRequestDTO();

        // Act
        dto.setCandidateId(3L);
        dto.setElectionId(4L);

        // Assert
        assertEquals(3L, dto.getCandidateId());
        assertEquals(4L, dto.getElectionId());
    }

    @Test
    void testVoteRequestDTO_EqualsAndHashCode() {
        // Arrange
        VoteRequestDTO dto1 = new VoteRequestDTO(1L, 2L);
        VoteRequestDTO dto2 = new VoteRequestDTO(1L, 2L);
        VoteRequestDTO dto3 = new VoteRequestDTO(3L, 2L);

        // Assert
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testVoteRequestDTO_ToString() {
        // Arrange
        VoteRequestDTO dto = new VoteRequestDTO(1L, 2L);

        // Act
        String toString = dto.toString();

        // Assert
        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("2"));
    }
}
