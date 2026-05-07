package com.toiletfinder.toilet_finder.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthChallengeCleanupService {

    private final JdbcTemplate jdbcTemplate;

    @Scheduled(fixedRate = 300000)
    public void cleanupExpiredChallenges() {
        String sql = """
            DELETE FROM auth_challenges
            WHERE expires_at < NOW()
        """;

        jdbcTemplate.update(sql);
    }
}
