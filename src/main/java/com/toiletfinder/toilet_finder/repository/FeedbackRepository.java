package com.toiletfinder.toilet_finder.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class FeedbackRepository {

    private final JdbcTemplate jdbcTemplate;

    public void save(

            UUID toiletId,

            UUID userId,

            String feedbackType
    ) {

        String sql = """
            INSERT INTO toilet_feedback (
                toilet_id,
                user_id,
                feedback_type,
                created_at
            )
            VALUES (?, ?, ?, now())
        """;

        jdbcTemplate.update(

                sql,

                toiletId,

                userId,

                feedbackType
        );
    }

    public boolean exists(

            UUID toiletId,

            UUID userId,

            String feedbackType
    ) {

        String sql = """
        SELECT EXISTS(

            SELECT 1
            FROM toilet_feedback
            WHERE toilet_id = ?
            AND user_id = ?
            AND feedback_type = ?
        )
    """;

        Boolean exists =
                jdbcTemplate.queryForObject(

                        sql,

                        Boolean.class,

                        toiletId,

                        userId,

                        feedbackType
                );

        return Boolean.TRUE.equals(exists);
    }

    public void delete(

            UUID toiletId,

            UUID userId,

            String feedbackType
    ) {

        String sql = """
        DELETE FROM toilet_feedback
        WHERE toilet_id = ?
        AND user_id = ?
        AND feedback_type = ?
    """;

        jdbcTemplate.update(

                sql,

                toiletId,

                userId,

                feedbackType
        );
    }
}