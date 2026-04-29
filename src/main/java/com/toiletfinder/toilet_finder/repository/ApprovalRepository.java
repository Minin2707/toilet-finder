package com.toiletfinder.toilet_finder.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ApprovalRepository {

    private final JdbcTemplate jdbcTemplate;

    //TODO исключить возможность апрува пользователем, добавившим точку
    public boolean alreadyApproved(UUID toiletId, UUID userId) {
        String sql = """
            SELECT COUNT(*)
            FROM approvals
            WHERE toilet_id = ? AND user_id = ?
        """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                toiletId,
                userId
        );

        return count != null && count > 0;
    }

    public void save(UUID toiletId, UUID userId) {
        String sql = """
            INSERT INTO approvals (
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

    public int countApprovals(UUID toiletId) {
        String sql = """
            SELECT COUNT(*)
            FROM approvals
            WHERE toilet_id = ?
        """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                toiletId
        );

        return count == null ? 0 : count;
    }
}
