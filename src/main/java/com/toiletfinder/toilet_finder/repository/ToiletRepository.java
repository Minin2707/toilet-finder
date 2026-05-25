package com.toiletfinder.toilet_finder.repository;

import com.toiletfinder.toilet_finder.dto.NearbyToiletResponse;
import com.toiletfinder.toilet_finder.mapper.NearbyToiletRowMapper;
import com.toiletfinder.toilet_finder.mapper.ToiletRowMapper;
import com.toiletfinder.toilet_finder.model.Toilet;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ToiletRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<NearbyToiletResponse> findNearby(

            double lat,

            double lon,

            int radiusMeters,

            int limit,

            Boolean approvedOnly,

            Boolean accessibleOnly,

            String accessType
    ) {

        StringBuilder sql =
                new StringBuilder("""
        SELECT
            id,
            title,
            description,
            address,
            status,
            created_at,
            access_type,
            wheelchair_accessible,
            confirmation_count,
            last_confirmed_at,
                                COALESCE((
                                    SELECT COUNT(*)
                                    FROM toilet_feedback tf
                                    WHERE tf.toilet_id = toilets.id
                                    AND tf.feedback_type = 'CLEAN'
                                ), 0) AS clean_count,
                                                   \s
                                COALESCE((
                                    SELECT COUNT(*)
                                    FROM toilet_feedback tf
                                    WHERE tf.toilet_id = toilets.id
                                    AND tf.feedback_type = 'DIRTY'
                                ), 0) AS dirty_count,
                       \s
                                COALESCE((
                                    SELECT COUNT(*)
                                    FROM toilet_feedback tf
                                    WHERE tf.toilet_id = toilets.id
                                    AND tf.feedback_type = 'HAS_PAPER'
                                ), 0) AS has_paper_count,
                       \s
                                COALESCE((
                                    SELECT COUNT(*)
                                    FROM toilet_feedback tf
                                    WHERE tf.toilet_id = toilets.id
                                    AND tf.feedback_type = 'WARM'
                                ), 0) AS warm_count,
                       \s
                                COALESCE((
                                    SELECT COUNT(*)
                                    FROM toilet_feedback tf
                                    WHERE tf.toilet_id = toilets.id
                                    AND tf.feedback_type = 'SAFE'
                                ), 0) AS safe_count,

            ST_Y(location::geometry) AS latitude,

            ST_X(location::geometry) AS longitude,

            ST_Distance(
                location,
                ST_SetSRID(
                    ST_MakePoint(?, ?),
                    4326
                )::geography
            ) AS distance_meters

        FROM toilets

        WHERE 1=1
   \s
        AND status != 'HIDDEN'
   \s""");

        if (Boolean.TRUE.equals(approvedOnly)) {

            sql.append("""
            AND status = 'APPROVED'
        """);
        }

        if (Boolean.TRUE.equals(accessibleOnly)) {

            sql.append("""
            AND wheelchair_accessible = true
        """);
        }

        if (accessType != null
                && !accessType.isBlank()) {

            sql.append("""
            AND access_type = ?
        """);
        }

        sql.append("""
        AND ST_DWithin(
            location,
            ST_SetSRID(
                ST_MakePoint(?, ?),
                4326
            )::geography,
            ?
        )

        ORDER BY location <-> ST_SetSRID(
            ST_MakePoint(?, ?),
            4326
        )::geography

        LIMIT ?
    """);

        List<Object> params =
                new ArrayList<>();

        params.add(lon);
        params.add(lat);

        if (accessType != null
                && !accessType.isBlank()) {

            params.add(accessType);
        }

        params.add(lon);
        params.add(lat);

        params.add(radiusMeters);

        params.add(lon);
        params.add(lat);

        params.add(limit);

        return jdbcTemplate.query(
                sql.toString(),
                new NearbyToiletRowMapper(),
                params.toArray()
        );
    }

    public void save(Toilet toilet) {

        String sql = """
        INSERT INTO toilets (
            id,
            title,
            description,
            location,
            address,
            status,
            access_type,
            wheelchair_accessible,
            created_at
        )
        VALUES (
            ?,
            ?,
            ?,

            ST_SetSRID(
                ST_MakePoint(?, ?),
                4326
            )::geography,

            ?,
            ?,
            ?,
            ?,
            ?
        )
    """;

        jdbcTemplate.update(
                sql,

                toilet.getId(),
                toilet.getTitle(),
                toilet.getDescription(),
                toilet.getLongitude(),
                toilet.getLatitude(),
                toilet.getAddress(),
                toilet.getStatus(),
                toilet.getAccessType(),
                toilet.getWheelchairAccessible(),
                toilet.getCreatedAt()
        );
    }

    public String findStatusById(UUID toiletId) {
        String sql = """
        SELECT status
        FROM toilets
        WHERE id = ?
    """;

        return jdbcTemplate.queryForObject(
                sql,
                String.class,
                toiletId
        );
    }

    public void updateStatus(UUID toiletId, String status) {
        String sql = """
        UPDATE toilets
        SET status = ?
        WHERE id = ?
    """;

        jdbcTemplate.update(
                sql,
                status,
                toiletId
        );
    }

    public void incrementReportCount(UUID toiletId) {

        String sql = """
        UPDATE toilets
        SET report_count = report_count + 1
        WHERE id = ?
    """;

        jdbcTemplate.update(
                sql,
                toiletId
        );
    }

    public Integer getReportCount(UUID toiletId) {

        String sql = """
        SELECT report_count
        FROM toilets
        WHERE id = ?
    """;

        return jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                toiletId
        );
    }

    public void confirm(UUID toiletId) {

        String sql = """
        UPDATE toilets
        SET
            confirmation_count = confirmation_count + 1,
            last_confirmed_at = NOW()
        WHERE id = ?
    """;

        jdbcTemplate.update(
                sql,
                toiletId
        );
    }

    public void resetRevalidationConfirmations(
            UUID toiletId
    ) {

        String sql = """
        UPDATE toilets
        SET revalidation_confirmation_count = 0
        WHERE id = ?
    """;

        jdbcTemplate.update(
                sql,
                toiletId
        );
    }

    public void resetReportCount(
            UUID toiletId
    ) {

        String sql = """
        UPDATE toilets
        SET report_count = 0
        WHERE id = ?
    """;

        jdbcTemplate.update(
                sql,
                toiletId
        );
    }

    public void incrementRevalidationConfirmations(
            UUID toiletId
    ) {

        String sql = """
        UPDATE toilets
        SET revalidation_confirmation_count =
            revalidation_confirmation_count + 1
        WHERE id = ?
    """;

        jdbcTemplate.update(
                sql,
                toiletId
        );
    }

    public int getRevalidationConfirmations(
            UUID toiletId
    ) {

        String sql = """
        SELECT revalidation_confirmation_count
        FROM toilets
        WHERE id = ?
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
