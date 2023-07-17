package com.modak.ratelimiterexercise.integration;

import com.modak.ratelimiterexercise.ratelimiter.RateLimitConfig;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RateLimitConfigTests {

    @Autowired
    private RateLimitConfig rateLimitConfig;

    @Test
    void testGetTokensForStatusOperationType() {
        Bucket bucket = rateLimitConfig.resolveBucket("status", "user1");
        long availableTokens = bucket.getAvailableTokens();
        Assertions.assertEquals(2, availableTokens);
    }

    @Test
    void testGetTokensForNewsOperationType() {
        Bucket bucket = rateLimitConfig.resolveBucket("news", "user1");
        long availableTokens = bucket.getAvailableTokens();
        Assertions.assertEquals(1, availableTokens);
    }

    @Test
    void testGetTokensForMarketingOperationType() {
        Bucket bucket = rateLimitConfig.resolveBucket("marketing", "user1");
        long availableTokens = bucket.getAvailableTokens();
        Assertions.assertEquals(3, availableTokens);
    }

    @Test
    void testGetTokensForUnknownOperationType() {
        Bucket bucket = rateLimitConfig.resolveBucket("undefined", "user1");
        long availableTokens = bucket.getAvailableTokens();
        Assertions.assertEquals(4, availableTokens);
    }

}
