package com.example;

import java.time.Instant;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        RateLimiter limiter = RateLimiter.createRateLimiter(5, 2);
        String user = "user123";

        // Burst 6 requests quickly
        for (int i = 1; i <= 6; i++) {
            boolean allowed = limiter.allowRequest(user, Instant.now().getEpochSecond());
            System.out.println("Request " + i + " allowed? " + allowed);
        }

        // Wait for leak
        Thread.sleep(2000);

        boolean allowed = limiter.allowRequest(user, Instant.now().getEpochSecond());
        System.out.println("After leak, request allowed? " + allowed);

        System.out.println("Bucket state: " + limiter.getBucketState(user));
    }
}
