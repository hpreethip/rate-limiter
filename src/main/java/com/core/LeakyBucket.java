package com.core;

import java.time.Instant;

public class LeakyBucket {
    private final int capacity;
    private final int rate; // leak rate (units per second)
    private int water;
    private long lastChecked; // Unix epoch seconds

    public LeakyBucket(int capacity, int rate, long timestamp) {
        this.capacity = capacity;
        this.rate = rate;
        this.water = 0;
        this.lastChecked = timestamp;
    }

    public LeakyBucket(int capacity, int rate) {
        this(capacity, rate, Instant.now().getEpochSecond());
    }

    public boolean allowRequest(long timestamp) {
        long elapsed = timestamp - lastChecked;
        int leaked = (int) (elapsed * rate);
        water = Math.max(0, water - leaked);
        lastChecked = timestamp;

        if (water < capacity) {
            water++;
            return true; // allowed
        }
        return false; // rejected
    }

    @Override
    public String toString() {
        return String.format("LeakyBucket{capacity=%d, rate=%d, water=%d, lastChecked=%d}",
                capacity, rate, water, lastChecked);
    }

}
