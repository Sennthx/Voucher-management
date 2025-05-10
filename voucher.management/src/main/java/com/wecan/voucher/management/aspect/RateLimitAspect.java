package com.wecan.voucher.management.aspect;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.wecan.voucher.management.annotations.RateLimited;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class RateLimitAspect {
    private final HttpServletRequest request;
    private final Cache<String, Integer> requestCounts;

    public RateLimitAspect(HttpServletRequest request) {
        this.request = request;
        this.requestCounts = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build();
    }

    @Around("@annotation(rateLimited)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimited rateLimited) throws Throwable {
        String ip = request.getRemoteAddr();
        int limit = rateLimited.requests();
        int window = rateLimited.seconds();

        Integer count = requestCounts.get(ip, k -> 0);
        if (count >= limit) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    String.format("Rate limit exceeded - %d requests per %d seconds", limit, window));
        }
        requestCounts.put(ip, count + 1);
        return joinPoint.proceed();
    }
}