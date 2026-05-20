package com.toiletfinder.toilet_finder.repository;

import com.toiletfinder.toilet_finder.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public void save(User user) {
        String sql = """
            INSERT INTO users(
                id,
                username,
                credential_id,
                public_key,
                created_at
            )
            VALUES (?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(
                sql,
                user.getId(),
                user.getUsername(),
                user.getCredentialId(),
                user.getPublicKey(),
                user.getCreatedAt()
        );
    }

    /*
     * Find user by username
     * Needed during login start flow
     */
    public User findByUsername(String username) {
        String sql = """
            SELECT id,
                   username,
                   credential_id,
                   public_key,
                   created_at
            FROM users
            WHERE username = ?
        """;

        List<User> users = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> mapUser(rs),
                username
        );

        return users.isEmpty() ? null : users.get(0);
    }

    /*
     * Find user by credentialId
     * Needed during login finish flow
     */
    public User findByCredentialId(String credentialId) {
        String sql = """
            SELECT id,
                   username,
                   credential_id,
                   public_key,
                   created_at
            FROM users
            WHERE credential_id = ?
        """;

        List<User> users = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> mapUser(rs),
                credentialId
        );

        return users.isEmpty() ? null : users.get(0);
    }

    public User findById(String id) {
        String sql = """
        SELECT *
        FROM users
        WHERE id = ?
    """;

        List<User> users = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> mapUser(rs),
                UUID.fromString(id)
        );

        return users.isEmpty()
                ? null
                : users.get(0);
    }

    /*
     * Shared mapper method
     */
    private User mapUser(java.sql.ResultSet rs)
            throws java.sql.SQLException {

        User user = new User();

        user.setId(
                UUID.fromString(rs.getString("id"))
        );

        user.setUsername(
                rs.getString("username")
        );

        user.setCredentialId(
                rs.getString("credential_id")
        );

        user.setPublicKey(
                rs.getString("public_key")
        );

        Timestamp timestamp =
                rs.getTimestamp("created_at");

        if (timestamp != null) {
            user.setCreatedAt(
                    timestamp.toLocalDateTime()
            );
        }

        return user;
    }

    public boolean existsById(UUID id) {

        String sql = """
        SELECT COUNT(*)
        FROM users
        WHERE id = ?
    """;

        Integer count =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class,
                        id
                );

        return count != null &&
                count > 0;
    }
}
