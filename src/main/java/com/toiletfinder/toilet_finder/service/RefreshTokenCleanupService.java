package com.toiletfinder.toilet_finder.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenCleanupService {

    private final JdbcTemplate jdbcTemplate;

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

        System.out.println(

                "Deleted refresh tokens: "
                        + deleted
        );
    }
}