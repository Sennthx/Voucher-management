package com.wecan.voucher.management.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimited {
    // Optional: Add configurable properties
    int value() default 10;        // Default: 10 requests
    int window() default 60;       // Default: 60 seconds
}