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

        String secret = Base64.getEncoder().encodeToString("supersecuresecretkeythatishardtoforge12345678901".getBytes());
        Mockito.when(jwtConfig.getSecret()).thenReturn(secret);
        Mockito.when(jwtConfig.getExpirationMs()).thenReturn(3600000L); // 1 hour

        jwtService = new JwtService(jwtConfig);
        jwtService.init();
    }

    @Test
    @DisplayName("Token should contain correct subject and role after generation")
    void generateTokenShouldContainCorrectUsernameAndRole() {
        String token = jwtService.generateToken("testUser");

        Claims claims = jwtService.validateToken(token);
        assertEquals("testUser", claims.getSubject());
        assertEquals("ADMIN", claims.get("role"));
        assertNotNull(claims.getExpiration());
    }

    @Test
    @DisplayName("Validation should fail when token is tampered with")
    void validateTokenShouldThrowForTamperedToken() {
        String token = jwtService.generateToken("user");
        String tampered = token.replace('a', 'b');

        RuntimeException ex = assertThrows(RuntimeException.class, () -> jwtService.validateToken(tampered));
        assertTrue(ex.getMessage().contains("Invalid or expired token"));
    }

    @Test
    @DisplayName("Should reject expired token")
    void shouldRejectExpiredToken() {
        Mockito.when(jwtConfig.getExpirationMs()).thenReturn(1L);
        jwtService.init();

        String token = jwtService.generateToken("testUser");

        try { Thread.sleep(2); } catch (InterruptedException ignored) {}
        assertThrows(RuntimeException.class, () -> jwtService.validateToken(token));
    }

    @Test
    @DisplayName("Should reject token with invalid signature")
    void shouldRejectInvalidSignature() {
        String otherSecret = Base64.getEncoder().encodeToString(
                "thisisadifferentsecretkeythatis32byteslong123456".getBytes()
        );

        Mockito.when(jwtConfig.getSecret()).thenReturn(otherSecret);
        JwtService otherService = new JwtService(jwtConfig);
        otherService.init();

        String foreignToken = otherService.generateToken("testUser");

        Mockito.when(jwtConfig.getSecret()).thenReturn(
                Base64.getEncoder().encodeToString(
                        "supersecuresecretkeythatishardtoforge1234567890".getBytes()
                )
        );

        assertThrows(RuntimeException.class, () -> jwtService.validateToken(foreignToken));
    }

    @Test
    @DisplayName("Should reject malformed JWT")
    void shouldRejectMalformedToken() {
        assertThrows(RuntimeException.class, () -> jwtService.validateToken("not.a.real.token"));
    }

    @Test
    @DisplayName("Should reject empty/null token")
    void shouldRejectEmptyToken() {
        assertThrows(RuntimeException.class, () -> jwtService.validateToken(""));
        assertThrows(RuntimeException.class, () -> jwtService.validateToken(null));
    }

    @Test
    @DisplayName("Should throw when secret key is too weak")
    void shouldThrowOnWeakSecretKey() {
        Mockito.when(jwtConfig.getSecret()).thenReturn("weak");
        JwtService weakKeyService = new JwtService(jwtConfig);
        assertThrows(io.jsonwebtoken.security.WeakKeyException.class, weakKeyService::init);
    }
}
