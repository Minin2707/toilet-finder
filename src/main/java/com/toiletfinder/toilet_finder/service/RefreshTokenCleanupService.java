package com.toiletfinder.toilet_finder.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class RefreshTokenCleanupService {

    private final JdbcTemplate jdbcTemplate;
    private static final Logger log =

            LoggerFactory.getLogger(
                    RefreshTokenCleanupService.class
            );

    @Scheduled(
            fixedRate = 86400000
    )
    public void cleanupRefreshTokens() {

        String sql = """
            DELETE FROM refresh_tokens
            WHERE revoked = true
               OR expires_at < NOW()
        """;

        int deleted =
                jdbcTemplate.update(sql);

        log.info(
                "Deleted refresh tokens: {}",
                deleted
        );
    }
}