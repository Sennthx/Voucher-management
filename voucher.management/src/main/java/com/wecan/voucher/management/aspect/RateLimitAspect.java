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
    private final Cache<String, RequestRecord> requestCounts;

    public RateLimitAspect(HttpServletRequest request) {
        this.request = request;
        this.requestCounts = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES) // long enough, actual window checked manually
                .build();
    }

    @Around("@annotation(rateLimited)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimited rateLimited) throws Throwable {
        String ip = request.getRemoteAddr();
        int limit = rateLimited.requests();
        int windowSeconds = rateLimited.seconds();

        long now = System.currentTimeMillis();

        RequestRecord record = requestCounts.get(ip, k -> new RequestRecord(0, now));

        if (now - record.firstRequestTime() > windowSeconds * 1000L) {
            // Window expired -> reset count
            record = new RequestRecord(1, now);
        } else if (record.count() >= limit) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    String.format("Rate limit exceeded - %d requests per %d seconds", limit, windowSeconds));
        } else {
            // Increment count
            record = new RequestRecord(record.count() + 1, record.firstRequestTime());
        }

        requestCounts.put(ip, record);
        return joinPoint.proceed();
    }

    private record RequestRecord(int count, long firstRequestTime) {}
}