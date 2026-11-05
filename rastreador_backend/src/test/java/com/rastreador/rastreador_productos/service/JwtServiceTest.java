package com.rastreador.rastreador_productos.service;

import com.rastreador.rastreador_productos.models.User;
import com.rastreador.rastreador_productos.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;
    private User user;

    // Clave secreta HMAC-SHA256 codificada en Base64 para pruebas
    private final String secretKeyBase64 = "NDBFRTYzNTI2NjU1NkE1OE5GMzJSNzVVNzg4NzhGMjFBNEY0NDI4NDcyQjRCNjJQNjRTNjc1NktCOTBP";
    private final long expiration = 86400000L; // 24 horas

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKeyBase64);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", expiration);

        user = new User("Test User", "testuser@example.com", "password");
    }

    @Test
   //getToken y extractUsername: Genera un JWT y extrae el username correctamente
    void shouldGenerateTokenAndExtractUsername() {
        String token = jwtService.getToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        String extractedUsername = jwtService.extractUsername(token);
        assertEquals("testuser@example.com", extractedUsername);
    }

    @Test
    //isTokenValid: Retorna true para un token válido asignado al mismo usuario
    void shouldValidateTokenCorrectly() {
        String token = jwtService.getToken(user);

        boolean isValid = jwtService.isTokenValid(token, user);
        assertTrue(isValid);
    }

    @Test
    //isTokenValid: Retorna false si el usuario no coincide con el sujeto del token
    void shouldReturnFalse_WhenUsernameDoesNotMatch() {
        String token = jwtService.getToken(user);
        User anotherUser = new User("Other", "other@example.com", "password");

        boolean isValid = jwtService.isTokenValid(token, anotherUser);
        assertFalse(isValid);
    }
}