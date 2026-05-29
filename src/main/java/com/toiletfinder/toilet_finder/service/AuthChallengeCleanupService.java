package com.toiletfinder.toilet_finder.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthChallengeCleanupService {

    private final JdbcTemplate jdbcTemplate;

    @Scheduled(fixedRate = 300000)
    public void cleanupExpiredChallenges() {

        String sql = """
        DELETE FROM auth_challenges
        WHERE expires_at < NOW()
    """;

        int deleted =
                jdbcTemplate.update(sql);

        if (deleted > 0) {

            log.info(
                    "Deleted expired auth challenges: {}",
                    deleted
            );
        }
    }
}
