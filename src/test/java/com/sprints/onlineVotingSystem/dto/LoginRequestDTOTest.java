package com.sprints.onlineVotingSystem.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestDTOTest {

    @Test
    void testLoginRequestDTO_ConstructorAndGetters() {
        // Arrange & Act
        LoginRequestDTO dto = new LoginRequestDTO("test@example.com", "password123");

        // Assert
        assertEquals("test@example.com", dto.getEmail());
        assertEquals("password123", dto.getPassword());
    }

    @Test
    void testLoginRequestDTO_NoArgsConstructor() {
        // Arrange & Act
        LoginRequestDTO dto = new LoginRequestDTO();

        // Assert
        assertNull(dto.getEmail());
        assertNull(dto.getPassword());
    }

    @Test
    void testLoginRequestDTO_Setters() {
        // Arrange
        LoginRequestDTO dto = new LoginRequestDTO();

        // Act
        dto.setEmail("new@example.com");
        dto.setPassword("newPassword");

        // Assert
        assertEquals("new@example.com", dto.getEmail());
        assertEquals("newPassword", dto.getPassword());
    }

    @Test
    void testLoginRequestDTO_EqualsAndHashCode() {
        // Arrange
        LoginRequestDTO dto1 = new LoginRequestDTO("test@example.com", "password123");
        LoginRequestDTO dto2 = new LoginRequestDTO("test@example.com", "password123");
        LoginRequestDTO dto3 = new LoginRequestDTO("different@example.com", "password123");

        // Assert
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testLoginRequestDTO_ToString() {
        // Arrange
        LoginRequestDTO dto = new LoginRequestDTO("test@example.com", "password123");

        // Act
        String toString = dto.toString();

        // Assert
        assertTrue(toString.contains("test@example.com"));
        assertTrue(toString.contains("password123"));
    }
}
