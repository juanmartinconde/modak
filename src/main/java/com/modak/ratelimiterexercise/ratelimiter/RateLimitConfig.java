package com.modak.ratelimiterexercise.ratelimiter;

import io.github.bucket4j.*;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitConfig {

    private static final Bandwidth defaultLimiter = Bandwidth.classic(4, Refill.intervally(4, Duration.ofMinutes(1)));
    private static final Bandwidth statusLimiter = Bandwidth.classic(2, Refill.intervally(2, Duration.ofMinutes(1)));
    private static final Bandwidth newsLimiter = Bandwidth.classic(1, Refill.intervally(1, Duration.ofDays(1)));
    private static final Bandwidth marketingLimiter = Bandwidth.classic(3, Refill.intervally(3, Duration.ofHours(1)));
    private ProxyManager<String> buckets;

    @Autowired
    public RateLimitConfig(ProxyManager<String> buckets) {
        this.buckets = buckets;
    }

    public Bucket resolveBucket(String type, String user) {
        String bucketKey = type + "-" + user;
        BucketConfiguration bucketConfig = getLimiterAccordingToOperationType(type);
        return buckets.builder().build(bucketKey, bucketConfig);
    }

    //private Supplier<BucketConfiguration> getLimiterAccordingToOperationType(String type) {
    private BucketConfiguration getLimiterAccordingToOperationType(String type) {
        ConfigurationBuilder bucketBuilder = BucketConfiguration.builder();
        switch (type) {
            case "status" -> bucketBuilder.addLimit(statusLimiter);
            case "news" -> bucketBuilder.addLimit(newsLimiter);
            case "marketing" -> bucketBuilder.addLimit(marketingLimiter);
            default -> bucketBuilder.addLimit(defaultLimiter);
        }
        return bucketBuilder.build();
    }

}
