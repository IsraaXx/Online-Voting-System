package com.sprints.onlineVotingSystem.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginResponseDTOTest {

    @Test
    void testLoginResponseDTO_ConstructorAndGetters() {
        // Arrange & Act
        LoginResponseDTO dto = new LoginResponseDTO("jwt.token.here", "test@example.com", "VOTER", "Login successful");

        // Assert
        assertEquals("jwt.token.here", dto.getToken());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals("VOTER", dto.getRole());
        assertEquals("Login successful", dto.getMessage());
    }

    @Test
    void testLoginResponseDTO_NoArgsConstructor() {
        // Arrange & Act
        LoginResponseDTO dto = new LoginResponseDTO();

        // Assert
        assertNull(dto.getToken());
        assertNull(dto.getEmail());
        assertNull(dto.getRole());
        assertNull(dto.getMessage());
    }

    @Test
    void testLoginResponseDTO_Setters() {
        // Arrange
        LoginResponseDTO dto = new LoginResponseDTO();

        // Act
        dto.setToken("new.jwt.token");
        dto.setEmail("new@example.com");
        dto.setRole("ADMIN");
        dto.setMessage("New message");

        // Assert
        assertEquals("new.jwt.token", dto.getToken());
        assertEquals("new@example.com", dto.getEmail());
        assertEquals("ADMIN", dto.getRole());
        assertEquals("New message", dto.getMessage());
    }

    @Test
    void testLoginResponseDTO_EqualsAndHashCode() {
        // Arrange
        LoginResponseDTO dto1 = new LoginResponseDTO("token", "email", "role", "message");
        LoginResponseDTO dto2 = new LoginResponseDTO("token", "email", "role", "message");
        LoginResponseDTO dto3 = new LoginResponseDTO("different", "email", "role", "message");

        // Assert
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testLoginResponseDTO_ToString() {
        // Arrange
        LoginResponseDTO dto = new LoginResponseDTO("jwt.token.here", "test@example.com", "VOTER", "Login successful");

        // Act
        String toString = dto.toString();

        // Assert
        assertTrue(toString.contains("jwt.token.here"));
        assertTrue(toString.contains("test@example.com"));
        assertTrue(toString.contains("VOTER"));
        assertTrue(toString.contains("Login successful"));
    }
}
