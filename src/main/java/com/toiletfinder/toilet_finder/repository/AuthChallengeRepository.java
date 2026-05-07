package com.toiletfinder.toilet_finder.repository;

import com.toiletfinder.toilet_finder.model.AuthChallenge;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AuthChallengeRepository {

    private final JdbcTemplate jdbcTemplate;

    public void save(AuthChallenge challenge) {
        String sql = """
            INSERT INTO auth_challenges (
                id,
                username,
                challenge,
                challenge_type,
                expires_at,
                created_at,
                options_json                         
            )
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(
                sql,
                challenge.getId(),
                challenge.getUsername(),
                challenge.getChallenge(),
                challenge.getChallengeType(),
                challenge.getExpiresAt(),
                challenge.getCreatedAt(),
                challenge.getOptionsJson()
        );
    }

    public AuthChallenge findValidChallenge(
            String username,
            String challengeType
    ) {
        String sql = """
            SELECT *
            FROM auth_challenges
            WHERE username = ?
              AND challenge_type = ?
              AND expires_at > NOW()
            ORDER BY created_at DESC
            LIMIT 1
        """;

        List<AuthChallenge> challenges = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    AuthChallenge challenge =
                            new AuthChallenge();

                    challenge.setId(
                            UUID.fromString(
                                    rs.getString("id")
                            )
                    );

                    challenge.setUsername(
                            rs.getString("username")
                    );

                    challenge.setChallenge(
                            rs.getString("challenge")
                    );

                    challenge.setChallengeType(
                            rs.getString("challenge_type")
                    );

                    challenge.setExpiresAt(
                            rs.getTimestamp("expires_at")
                                    .toLocalDateTime()
                    );

                    challenge.setCreatedAt(
                            rs.getTimestamp("created_at")
                                    .toLocalDateTime()
                    );

                    challenge.setOptionsJson(
                            rs.getString("options_json")
                    );

                    return challenge;
                },
                username,
                challengeType
        );

        return challenges.isEmpty()
                ? null
                : challenges.get(0);
    }

    public void deleteByUsernameAndType(
            String username,
            String challengeType
    ) {
        String sql = """
            DELETE FROM auth_challenges
            WHERE username = ?
              AND challenge_type = ?
        """;

        jdbcTemplate.update(
                sql,
                username,
                challengeType
        );
    }
}
