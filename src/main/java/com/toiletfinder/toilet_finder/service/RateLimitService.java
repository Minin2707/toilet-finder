package com.toiletfinder.toilet_finder.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final ConcurrentMap<UUID, Bucket>
            createToiletBuckets =
            new ConcurrentHashMap<>();

    private final ConcurrentMap<UUID, Bucket>
            feedbackBuckets =
            new ConcurrentHashMap<>();

    private final ConcurrentMap<UUID, Bucket>
            reportBuckets =
            new ConcurrentHashMap<>();

    private final ConcurrentMap<UUID, Bucket>
            approveBuckets =
            new ConcurrentHashMap<>();

    public Bucket resolveCreateToiletBucket(
            UUID userId
    ) {

        return createToiletBuckets.computeIfAbsent(

                userId,

                id -> Bucket.builder()
                        .addLimit(
                                Bandwidth.builder()

                                        .capacity(10)

                                        .refillGreedy(
                                                10,
                                                Duration.ofHours(1)
                                        )

                                        .build()
                        )
                        .build()
        );
    }

    public Bucket resolveFeedbackBucket(
            UUID userId
    ) {

        return feedbackBuckets.computeIfAbsent(

                userId,

                id -> Bucket.builder()
                        .addLimit(
                                Bandwidth.builder()

                                        .capacity(30)

                                        .refillGreedy(
                                                30,
                                                Duration.ofMinutes(1)
                                        )

                                        .build()
                        )
                        .build()
        );
    }

    public Bucket resolveReportBucket(
            UUID userId
    ) {

        return reportBuckets.computeIfAbsent(

                userId,

                id -> Bucket.builder()
                        .addLimit(
                                Bandwidth.builder()

                                        .capacity(10)

                                        .refillGreedy(
                                                10,
                                                Duration.ofMinutes(1)
                                        )

                                        .build()
                        )
                        .build()
        );
    }

    public Bucket resolveApproveBucket(
            UUID userId
    ) {

        return approveBuckets.computeIfAbsent(

                userId,

                id -> Bucket.builder()
                        .addLimit(
                                Bandwidth.builder()

                                        .capacity(20)

                                        .refillGreedy(
                                                20,
                                                Duration.ofMinutes(1)
                                        )

                                        .build()
                        )
                        .build()
        );
    }
}