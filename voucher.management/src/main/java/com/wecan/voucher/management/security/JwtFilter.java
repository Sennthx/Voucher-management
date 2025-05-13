package com.wecan.voucher.management.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private RequestMatcher[] skipMatchers;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public void setSkipMatchers(RequestMatcher... skipMatchers) {
        this.skipMatchers = skipMatchers;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (skipMatchers == null) {
            return false;
        }
        return Arrays.stream(skipMatchers)
                .anyMatch(matcher -> matcher.matches(request));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws IOException, ServletException {

        if (shouldNotFilter(request)) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
            return;
        }

        try {
            String token = authHeader.substring(7);

            if (token.isEmpty()) {
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "Empty token provided");
                return;
            }

            Claims claims = jwtService.validateToken(token);

            if (!"ADMIN".equals(claims.get("role"))) {
                sendError(response, HttpServletResponse.SC_FORBIDDEN, "Admin role required");
                return;
            }

            // Create authentication token
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    claims.getSubject(),
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + claims.get("role")))
            );

            // Set authentication in SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);

        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
        }
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}