package com.wecan.voucher.management.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

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

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should identify public endpoints correctly")
    void shouldIdentifyPublicEndpoints() {

        jwtFilter.setSkipMatchers(new AntPathRequestMatcher("/auth/login", "POST"));

        MockHttpServletRequest publicRequest = new MockHttpServletRequest("POST", "/auth/login");
        publicRequest.setServletPath("/auth/login");

        MockHttpServletRequest privateRequest = new MockHttpServletRequest("GET", "/api/vouchers");
        privateRequest.setServletPath("/api/vouchers");

        assertTrue(jwtFilter.shouldNotFilter(publicRequest));
        assertFalse(jwtFilter.shouldNotFilter(privateRequest));
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

    @Test
    @DisplayName("Should reject empty token")
    void shouldRejectEmptyToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/vouchers");
        request.addHeader("Authorization", "Bearer ");

        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtFilter.doFilter(request, response, new MockFilterChain());

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Empty token provided"));
    }

    @Test
    @DisplayName("Should handle different http methods")
    void shouldHandleDifferentHttpMethods() {

        MockHttpServletRequest getRequest = new MockHttpServletRequest("GET", "/auth/login");
        getRequest.setServletPath("/auth/login");
        assertFalse(jwtFilter.shouldNotFilter(getRequest));
    }

    @Test
    @DisplayName("Should handle multiple public endpoints")
    void shouldHandleMultiplePublicEndpoints() {
        jwtFilter.setSkipMatchers(
                new AntPathRequestMatcher("/auth/login", "POST"),
                new AntPathRequestMatcher("/public/resource")
        );

        MockHttpServletRequest publicResourceRequest = new MockHttpServletRequest("GET", "/public/resource");
        publicResourceRequest.setServletPath("/public/resource");
        assertTrue(jwtFilter.shouldNotFilter(publicResourceRequest));
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
    @DisplayName("Should reject malformed Authorization header")
    void shouldRejectMalformedHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/vouchers");
        request.addHeader("Authorization", "InvalidHeader");

        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtFilter.doFilter(request, response, new MockFilterChain());

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Missing or invalid Authorization header"));
    }

    @Test
    @DisplayName("Error responses should have application/json content type")
    void errorResponsesShouldHaveJsonContentType() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/vouchers");
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtFilter.doFilter(request, response, new MockFilterChain());

        assertEquals("application/json", response.getContentType());
    }

    @Test
    @DisplayName("Should handle null skip matcher")
    void shouldHandleNullSkipMatchers() {
        jwtFilter.setSkipMatchers(null);
        MockHttpServletRequest anyRequest = new MockHttpServletRequest("GET", "/any/path");
        anyRequest.setServletPath("/any/path");
        assertFalse(jwtFilter.shouldNotFilter(anyRequest));
    }


}
