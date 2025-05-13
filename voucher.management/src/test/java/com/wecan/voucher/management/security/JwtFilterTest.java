package com.wecan.voucher.management.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtFilterTest {

    private JwtService jwtService;
    private JwtFilter jwtFilter;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        jwtFilter = new JwtFilter(jwtService);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should bypass filter for public endpoints")
    void shouldSkipAuthenticationForPublicEndpoints() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/auth/login");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        jwtFilter.doFilter(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Should reject missing Authorization header")
    void shouldRejectMissingAuthHeader() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/vouchers");
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtFilter.doFilter(request, response, new MockFilterChain());

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Missing or invalid Authorization header"));
    }

    @Test
    @DisplayName("Should reject token without ADMIN role")
    void shouldRejectTokenWithoutAdminRole() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/vouchers");
        request.addHeader("Authorization", "Bearer token");

        MockHttpServletResponse response = new MockHttpServletResponse();

        Claims claims = mock(Claims.class);
        when(jwtService.validateToken("token")).thenReturn(claims);
        when(claims.get("role")).thenReturn("USER");
        when(claims.getSubject()).thenReturn("testUser");

        jwtFilter.doFilter(request, response, new MockFilterChain());

        assertEquals(403, response.getStatus());
        assertTrue(response.getContentAsString().contains("Admin role required"));
    }

    @Test
    @DisplayName("Should authenticate valid token with ADMIN role")
    void shouldAuthenticateValidAdminToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/vouchers");
        request.addHeader("Authorization", "Bearer validtoken");
        MockHttpServletResponse response = new MockHttpServletResponse();

        Claims claims = mock(Claims.class);
        when(jwtService.validateToken("validtoken")).thenReturn(claims);
        when(claims.get("role")).thenReturn("ADMIN");
        when(claims.getSubject()).thenReturn("adminUser");

        MockFilterChain chain = new MockFilterChain();

        jwtFilter.doFilter(request, response, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("adminUser", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        assertTrue(SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
    }

    @Test
    @DisplayName("Should reject invalid token")
    void shouldRejectInvalidToken() throws Exception {
        MockHttpServletRequest request =
                new MockHttpServletRequest("GET", "/api/vouchers");
        request.addHeader("Authorization", "Bearer badtoken");

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.validateToken("badtoken"))
                .thenThrow(new RuntimeException("Invalid or expired token"));

        jwtFilter.doFilter(request, response, new MockFilterChain());

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Invalid or expired token"));
    }
}
