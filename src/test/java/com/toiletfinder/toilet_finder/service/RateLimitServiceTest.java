package com.toiletfinder.toilet_finder.service;

import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RateLimitServiceTest {

    private final RateLimitService rateLimitService =
            new RateLimitService();

    @Test
    void shouldBlockApproveAfterLimitExceeded() {

        UUID userId =
                UUID.randomUUID();

        Bucket bucket =
                rateLimitService
                        .resolveApproveBucket(
                                userId
                        );

        for (int i = 0; i < 20; i++) {

            assertTrue(
                    bucket.tryConsume(1)
            );
        }

        assertFalse(
                bucket.tryConsume(1)
        );
    }
}
