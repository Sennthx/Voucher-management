package com.wecan.voucher.management.voucherSystem.controller;

import com.wecan.voucher.management.config.AdminConfigProperties;
import com.wecan.voucher.management.security.JwtService;
import com.wecan.voucher.management.voucherSystem.dto.request.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private AdminConfigProperties adminConfig;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    private final String TEST_USERNAME = "admin";
    private final String TEST_PASSWORD = "securePassword";
    private final String TEST_TOKEN = "test.jwt.token";

    @BeforeEach
    void setUp() {

    }

    @Test
    @DisplayName("Should return token for valid credentials")
    void loginShouldReturnTokenForValidCredentials() {
        when(adminConfig.username()).thenReturn(TEST_USERNAME);
        when(adminConfig.password()).thenReturn("hashedPassword");

        LoginRequest request = new LoginRequest(TEST_USERNAME, TEST_PASSWORD);
        when(passwordEncoder.matches(TEST_PASSWORD, "hashedPassword")).thenReturn(true);
        when(jwtService.generateToken(TEST_USERNAME)).thenReturn(TEST_TOKEN);

        ResponseEntity<Map<String, String>> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(TEST_TOKEN, response.getBody().get("token"));
        assertEquals("ADMIN", response.getBody().get("role"));
        verify(jwtService).generateToken(TEST_USERNAME);
    }

    @Test
    @DisplayName("Should return 401 for invalid username")
    void loginShouldReturn401ForInvalidUsername() {
        when(adminConfig.username()).thenReturn(TEST_USERNAME);

        LoginRequest request = new LoginRequest("wrongUser", TEST_PASSWORD);
        ResponseEntity<Map<String, String>> response = authController.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid username or password", response.getBody().get("error"));
        verify(jwtService, never()).generateToken(anyString());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("Should return 401 for invalid password")
    void loginShouldReturn401ForInvalidPassword() {
        when(adminConfig.username()).thenReturn(TEST_USERNAME);
        when(adminConfig.password()).thenReturn("hashedPassword");

        LoginRequest request = new LoginRequest(TEST_USERNAME, "wrongPassword");
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        ResponseEntity<Map<String, String>> response = authController.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid username or password", response.getBody().get("error"));
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("Should return 401 when password matches but username is wrong")
    void loginShouldReturn401WhenPasswordMatchesButUsernameWrong() {
        when(adminConfig.username()).thenReturn(TEST_USERNAME);

        LoginRequest request = new LoginRequest("wrongUser", TEST_PASSWORD);

        ResponseEntity<Map<String, String>> response = authController.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid username or password", response.getBody().get("error"));
        verify(jwtService, never()).generateToken(anyString());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("Should return 401 when username matches but password is wrong")
    void loginShouldReturn401WhenUsernameMatchesButPasswordWrong() {
        when(adminConfig.username()).thenReturn(TEST_USERNAME);
        when(adminConfig.password()).thenReturn("hashedPassword");

        LoginRequest request = new LoginRequest(TEST_USERNAME, "wrongPassword");
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        ResponseEntity<Map<String, String>> response = authController.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid username or password", response.getBody().get("error"));
        verify(jwtService, never()).generateToken(anyString());
    }
}