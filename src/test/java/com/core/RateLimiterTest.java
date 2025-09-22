package com.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RateLimiterTest {

    // First request from new user → allowed
    @Test
    void testFirstRequestFromNewUser() {
        RateLimiter limiter = RateLimiter.createRateLimiter(5, 1);
        boolean allowed = limiter.allowRequest("user1", 0);
        assertTrue(allowed, "First request from a new user should be allowed");
    }

    // Basic functionality → small requests pass
    @Test
    void testBasicFunctionality() {
        RateLimiter limiter = RateLimiter.createRateLimiter(5, 1);
        assertTrue(limiter.allowRequest("user1", 0));
        assertTrue(limiter.allowRequest("user1", 1));
    }

    // Burst handling / overflow → rejects beyond capacity
    @Test
    void testBurstHandlingOverflow() {
        RateLimiter limiter = RateLimiter.createRateLimiter(3, 1); // capacity=3
        assertTrue(limiter.allowRequest("user1", 0));
        assertTrue(limiter.allowRequest("user1", 0));
        assertTrue(limiter.allowRequest("user1", 0));
        assertFalse(limiter.allowRequest("user1", 0), "4th request should be rejected due to overflow");
    }

    // Time-based leaking → frees up after elapsed time
    @Test
    void testTimeBasedLeaking() {
        RateLimiter limiter = RateLimiter.createRateLimiter(2, 1); // leak 1 per sec
        assertTrue(limiter.allowRequest("user1", 0));
        assertTrue(limiter.allowRequest("user1", 0));
        assertFalse(limiter.allowRequest("user1", 0)); // full

        // Jump forward 2 seconds -> bucket should leak 2
        assertTrue(limiter.allowRequest("user1", 2));
        assertTrue(limiter.allowRequest("user1", 2));
    }

    // Multiple users → independent buckets
    @Test
    void testMultipleUsersIndependentBuckets() {
        RateLimiter limiter = RateLimiter.createRateLimiter(2, 1);

        // user1 fills their bucket
        assertTrue(limiter.allowRequest("user1", 0));
        assertTrue(limiter.allowRequest("user1", 0));
        assertFalse(limiter.allowRequest("user1", 0));

        // user2 should be independent
        assertTrue(limiter.allowRequest("user2", 0));
        assertTrue(limiter.allowRequest("user2", 0));
    }

    // Backwards timestamps → prevents bypassing
    @Test
    void testTimestampsGoBackwards() {
        RateLimiter limiter = RateLimiter.createRateLimiter(2, 1);

        assertTrue(limiter.allowRequest("user1", 1));
        assertTrue(limiter.allowRequest("user1", 1));

        // Move backwards in time
        boolean allowed = limiter.allowRequest("user1", 0);
        assertFalse(allowed, "Going backwards in time should not bypass the bucket capacity");
    }

    // Large time gaps → bucket empties completely
    @Test
    void testLargeTimeGaps() {
        RateLimiter limiter = RateLimiter.createRateLimiter(5, 1);

        // Fill the bucket
        for (int i = 0; i < 5; i++) {
            assertTrue(limiter.allowRequest("user1", 0));
        }
        assertFalse(limiter.allowRequest("user1", 0));

        // Jump 10 seconds -> bucket should be completely empty
        assertTrue(limiter.allowRequest("user1", 10));
    }
}
