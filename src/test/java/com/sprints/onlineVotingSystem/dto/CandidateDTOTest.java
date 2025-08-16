package com.sprints.onlineVotingSystem.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CandidateDTOTest {

    @Test
    void testCandidateDTO_ConstructorAndGetters() {
        // Arrange & Act
        CandidateDTO dto = new CandidateDTO(1L, "John Doe", "Democratic Party", "Presidential Election 2024");

        // Assert
        assertEquals(1L, dto.getId());
        assertEquals("John Doe", dto.getName());
        assertEquals("Democratic Party", dto.getParty());
        assertEquals("Presidential Election 2024", dto.getElectionName());
    }

    @Test
    void testCandidateDTO_NoArgsConstructor() {
        // Arrange & Act
        CandidateDTO dto = new CandidateDTO();

        // Assert
        assertNull(dto.getId());
        assertNull(dto.getName());
        assertNull(dto.getParty());
        assertNull(dto.getElectionName());
    }

    @Test
    void testCandidateDTO_Setters() {
        // Arrange
        CandidateDTO dto = new CandidateDTO();

        // Act
        dto.setId(2L);
        dto.setName("Jane Smith");
        dto.setParty("Republican Party");
        dto.setElectionName("Senate Election 2024");

        // Assert
        assertEquals(2L, dto.getId());
        assertEquals("Jane Smith", dto.getName());
        assertEquals("Republican Party", dto.getParty());
        assertEquals("Senate Election 2024", dto.getElectionName());
    }

    @Test
    void testCandidateDTO_EqualsAndHashCode() {
        // Arrange
        CandidateDTO dto1 = new CandidateDTO(1L, "John Doe", "Democratic Party", "Presidential Election 2024");
        CandidateDTO dto2 = new CandidateDTO(1L, "John Doe", "Democratic Party", "Presidential Election 2024");
        CandidateDTO dto3 = new CandidateDTO(2L, "John Doe", "Democratic Party", "Presidential Election 2024");

        // Assert
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testCandidateDTO_ToString() {
        // Arrange
        CandidateDTO dto = new CandidateDTO(1L, "John Doe", "Democratic Party", "Presidential Election 2024");

        // Act
        String toString = dto.toString();

        // Assert
        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("John Doe"));
        assertTrue(toString.contains("Democratic Party"));
        assertTrue(toString.contains("Presidential Election 2024"));
    }
}
