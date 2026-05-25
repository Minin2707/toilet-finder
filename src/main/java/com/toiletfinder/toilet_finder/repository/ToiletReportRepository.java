package com.toiletfinder.toilet_finder.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ToiletReportRepository {

    private final JdbcTemplate jdbcTemplate;

    public void save(
            UUID toiletId,
            UUID userId
    ) {

        String sql = """
            INSERT INTO toilet_reports (
                id,
                toilet_id,
                user_id,
                created_at
            )
            VALUES (?, ?, ?, ?)
        """;

        jdbcTemplate.update(

                sql,

                UUID.randomUUID(),

                toiletId,

                userId,

                LocalDateTime.now()
        );
    }

    public int countReports(UUID toiletId) {

        String sql = """
            SELECT COUNT(*)
            FROM toilet_reports
            WHERE toilet_id = ?
        """;

        Integer count =
                jdbcTemplate.queryForObject(

                        sql,

                        Integer.class,

                        toiletId
                );

        return count == null ? 0 : count;
    }
}