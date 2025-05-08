package com.wecan.voucher.management.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "admin")
public record AdminConfigProperties(
        String username,
        String password
) {}