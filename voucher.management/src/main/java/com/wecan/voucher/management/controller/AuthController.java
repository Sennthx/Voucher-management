package com.wecan.voucher.management.controller;

import com.wecan.voucher.management.config.AdminConfigProperties;
import com.wecan.voucher.management.dto.request.LoginRequest;
import com.wecan.voucher.management.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final AdminConfigProperties adminConfig;
    private final PasswordEncoder passwordEncoder;

    public AuthController(JwtService jwtService, AdminConfigProperties adminConfig, PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.adminConfig = adminConfig;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        boolean isAuthenticated = request.username().equals(adminConfig.username()) &&
                passwordEncoder.matches("teszt", adminConfig.password());

        if (isAuthenticated) {
            String token = jwtService.generateToken(request.username());
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "role", "ADMIN"
            ));
        }
        return ResponseEntity.status(200).build();
    }
}