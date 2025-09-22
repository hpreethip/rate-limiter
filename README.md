# Rate Limiter

Implementation of a rate limiter using the Leaky Bucket algorithm.

### Scope

- **Leaky Bucket**: Fixed-capacity bucket that leaks at a constant rate (requests/sec)
- **Per-user isolation**: Each `userId` gets its own bucket
- **Deterministic time control**: API accepts a timestamp (epoch seconds) so you can simulate time in tests or use real time in production

## Algorithm

The Leaky Bucket holds up to `capacity` requests. The bucket leaks at `rate` units per second. For each request at time `t`:

- Compute elapsed seconds since `lastChecked`
- Reduce water by `elapsed * rate` (not below 0)
- If `water < capacity`, increment water and allow; otherwise reject

This smooths bursts while keeping a steady outflow.

## Quick start

Requires Java 17+ and Maven.

```bash
# From the project root of this module
cd rate-limiter

# Compile
mvn -DskipTests compile

# Run the demo
java -cp target/classes com.core.Main
```

Expected output (will vary slightly by timing):

```text
Request 1 allowed? true
Request 2 allowed? true
Request 3 allowed? true
Request 4 allowed? true
Request 5 allowed? true
Request 6 allowed? false
After leak, request allowed? true
Bucket state: LeakyBucket{capacity=5, rate=2, water=2, lastChecked=...}
```

## Usage

```java
import com.core.RateLimiter;

// capacity = 5 tokens; leakRate = 2 tokens per second
RateLimiter limiter = RateLimiter.createRateLimiter(5, 2);

String userId = "user123";
long now = java.time.Instant.now().getEpochSecond();

boolean allowed = limiter.allowRequest(userId, now);
if (allowed) {
    // proceed
} else {
    // reject
}
```

## Run unit tests:

```bash
mvn test
```

## Configuration

- `capacity` (int): Maximum number of concurrent requests the bucket can hold
- `leakRate` (int): Units leaked per second (throughput). Higher values drain faster.

Choose values based on desired steady-state throughput and tolerated burst size. For example, `capacity=100, leakRate=10` allows a burst of 100 and a sustained rate of ~10 req/s.
