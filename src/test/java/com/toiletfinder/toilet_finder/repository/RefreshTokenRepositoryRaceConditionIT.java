package com.toiletfinder.toilet_finder.repository;

import com.toiletfinder.toilet_finder.AbstractPostgresIntegrationTest;
import com.toiletfinder.toilet_finder.model.RefreshToken;
import com.toiletfinder.toilet_finder.security.TokenHashService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RefreshTokenRepositoryRaceConditionIT
        extends AbstractPostgresIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private TokenHashService tokenHashService;

    private UUID createUser() {

        UUID userId =
                UUID.randomUUID();

        jdbcTemplate.update(
                """
                INSERT INTO users (
                    id,
                    username,
                    credential_id,
                    public_key,
                    created_at
                )
                VALUES (?, ?, ?, ?, now())
                """,
                userId,
                "user-" + UUID.randomUUID(),
                "credential-" + UUID.randomUUID(),
                "public-key"
        );

        return userId;
    }

    @Test
    void shouldAllowOnlyOneThreadToRevokeToken()
            throws Exception {

        UUID userId =
                createUser();

        String rawToken =
                UUID.randomUUID().toString();

        RefreshToken token =
                new RefreshToken();

        token.setId(
                UUID.randomUUID()
        );

        token.setUserId(
                userId
        );

        token.setTokenHash(
                tokenHashService.hash(
                        rawToken
                )
        );

        token.setCreatedAt(
                LocalDateTime.now()
        );

        token.setExpiresAt(
                LocalDateTime.now()
                        .plusDays(30)
        );

        token.setRevoked(false);

        refreshTokenRepository.save(
                token
        );

        int threads = 5;

        ExecutorService executor =
                Executors.newFixedThreadPool(
                        threads
                );

        CountDownLatch ready =
                new CountDownLatch(
                        threads
                );

        CountDownLatch start =
                new CountDownLatch(1);

        AtomicInteger successCount =
                new AtomicInteger();

        AtomicInteger failCount =
                new AtomicInteger();

        for (int i = 0;
             i < threads;
             i++) {

            executor.submit(() -> {

                ready.countDown();

                start.await();

                boolean success =
                        refreshTokenRepository
                                .revokeTokenIfActive(
                                        token.getId()
                                );

                if (success) {

                    successCount.incrementAndGet();

                } else {

                    failCount.incrementAndGet();
                }

                return null;
            });
        }

        ready.await();

        start.countDown();

        executor.shutdown();

        assertTrue(
                executor.awaitTermination(
                        10,
                        TimeUnit.SECONDS
                )
        );

        assertEquals(
                1,
                successCount.get()
        );

        assertEquals(
                4,
                failCount.get()
        );
    }

}
