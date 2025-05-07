package com.wecan.voucher.management.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    public String generateToken(UserDetails userDetails) {
        // JWT creation logic
        return "";
    }

    public boolean validateToken(String token) {
        // JWT verification logic
        return true;
    }
}