package com.toiletfinder.toilet_finder.repository;

import com.toiletfinder.toilet_finder.model.UserMessage;
import com.toiletfinder.toilet_finder.enumStatus.UserMessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserMessageRepository {

    private final JdbcTemplate jdbcTemplate;

    public void save(UserMessage userMessage) {

        String sql = """
            INSERT INTO user_messages(
                id,
                user_id,
                type,
                message,
                created_at
            )
            VALUES (?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(
                sql,
                userMessage.getId(),
                userMessage.getUserId(),
                userMessage.getType().name(),
                userMessage.getMessage(),
                userMessage.getCreatedAt()
        );
    }

    public List<UserMessage> findAll() {

        String sql = """
            SELECT *
            FROM user_messages
            ORDER BY created_at DESC
        """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> mapUserMessage(rs)
        );
    }

    private UserMessage mapUserMessage(
            java.sql.ResultSet rs
    ) throws java.sql.SQLException {

        UserMessage userMessage =
                new UserMessage();

        userMessage.setId(
                UUID.fromString(
                        rs.getString("id")
                )
        );

        userMessage.setUserId(
                UUID.fromString(
                        rs.getString("user_id")
                )
        );

        userMessage.setType(
                UserMessageType.valueOf(
                        rs.getString("type")
                )
        );

        userMessage.setMessage(
                rs.getString("message")
        );

        Timestamp timestamp =
                rs.getTimestamp("created_at");

        if (timestamp != null) {

            userMessage.setCreatedAt(
                    timestamp.toLocalDateTime()
            );
        }

        return userMessage;
    }
}
