package com.wecan.voucher.management.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;


class JwtServiceTest {

    private JwtService jwtService;
    private JwtConfig jwtConfig;

    @BeforeEach
    void setUp() {
        jwtConfig = Mockito.mock(JwtConfig.class);

        String secret = Base64.getEncoder().encodeToString("supersecuresecretkeythatishardtoforge1234567890".getBytes());
        Mockito.when(jwtConfig.getSecret()).thenReturn(secret);
        Mockito.when(jwtConfig.getExpirationMs()).thenReturn(3600000L); // 1 hour

        jwtService = new JwtService(jwtConfig);
        jwtService.init();
    }

    @Test
    @DisplayName("Token should contain correct subject and role when generated")
    void generateTokenShouldContainCorrectUsernameAndRole() {
        String token = jwtService.generateToken("testUser");

        Claims claims = jwtService.validateToken(token);
        assertEquals("testUser", claims.getSubject());
        assertEquals("ADMIN", claims.get("role"));
        assertNotNull(claims.getExpiration());
    }

    @Test
    @DisplayName("Validation should fail when token is tampered")
    void validateTokenShouldThrowForTamperedToken() {
        String token = jwtService.generateToken("user");
        String tampered = token.replace('a', 'b');

        RuntimeException ex = assertThrows(RuntimeException.class, () -> jwtService.validateToken(tampered));
        assertTrue(ex.getMessage().contains("Invalid or expired token"));
    }
}
