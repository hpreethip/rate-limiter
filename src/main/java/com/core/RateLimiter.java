package com.core;

import java.util.HashMap;
import java.util.Map;

public class RateLimiter {
    private final int capacity;
    private final int leakRate;
    private final Map<String, LeakyBucket> userBuckets;

    public RateLimiter(int capacity, int leakRate) {
        this.capacity = capacity;
        this.leakRate = leakRate;
        this.userBuckets = new HashMap<>();
    }

    private LeakyBucket getOrCreateBucket(String userId, long timestamp) {
        return userBuckets.computeIfAbsent(
                userId,
                id -> new LeakyBucket(capacity, leakRate, timestamp));
    }

    public boolean allowRequest(String userId, long timestamp) {
        LeakyBucket bucket = getOrCreateBucket(userId, timestamp);
        return bucket.allowRequest(timestamp);
    }

    public String getBucketState(String userId) {
        LeakyBucket bucket = userBuckets.get(userId);
        return bucket == null ? null : bucket.toString();
    }

    public static RateLimiter createRateLimiter(int capacity, int leakRate) {
        return new RateLimiter(capacity, leakRate);
    }
}
