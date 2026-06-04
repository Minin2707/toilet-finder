package com.toiletfinder.toilet_finder.repository;

import com.toiletfinder.toilet_finder.model.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final JdbcTemplate jdbcTemplate;

    public void save(
            RefreshToken refreshToken
    ) {

        String sql = """
            INSERT INTO refresh_tokens (

                id,
                user_id,
                token_hash,
                expires_at,
                revoked,
                created_at
            )
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(

                sql,

                refreshToken.getId(),

                refreshToken.getUserId(),

                refreshToken.getTokenHash(),

                refreshToken.getExpiresAt(),

                refreshToken.isRevoked(),

                refreshToken.getCreatedAt()
        );
    }

    public RefreshToken findByTokenHash(
            String tokenHash
    ) {

        String sql = """
            SELECT *
            FROM refresh_tokens
            WHERE token_hash = ?
            LIMIT 1
        """;

        List<RefreshToken> tokens =
                jdbcTemplate.query(

                        sql,

                        (rs, rowNum) ->
                                mapRefreshToken(rs),

                        tokenHash
                );

        return tokens.isEmpty()
                ? null
                : tokens.get(0);
    }


    private RefreshToken mapRefreshToken(
            java.sql.ResultSet rs
    ) throws java.sql.SQLException {

        RefreshToken token =
                new RefreshToken();

        token.setId(

                UUID.fromString(
                        rs.getString("id")
                )
        );

        token.setUserId(

                UUID.fromString(
                        rs.getString("user_id")
                )
        );

        token.setTokenHash(
                rs.getString("token_hash")
        );

        token.setExpiresAt(

                rs.getTimestamp("expires_at")
                        .toLocalDateTime()
        );

        token.setRevoked(
                rs.getBoolean("revoked")
        );

        Timestamp createdAt =
                rs.getTimestamp("created_at");

        if (createdAt != null) {

            token.setCreatedAt(
                    createdAt.toLocalDateTime()
            );
        }

        return token;
    }

    public boolean revokeTokenIfActive(
            UUID tokenId
    ) {

        String sql = """
        UPDATE refresh_tokens
        SET revoked = true
        WHERE id = ?
        AND revoked = false
    """;

        int updatedRows =
                jdbcTemplate.update(
                        sql,
                        tokenId
                );

        return updatedRows == 1;
    }
}