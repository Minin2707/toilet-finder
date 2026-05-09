package com.toiletfinder.toilet_finder.repository;

import com.toiletfinder.toilet_finder.dto.NearbyToiletResponse;
import com.toiletfinder.toilet_finder.mapper.NearbyToiletRowMapper;
import com.toiletfinder.toilet_finder.mapper.ToiletRowMapper;
import com.toiletfinder.toilet_finder.model.Toilet;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
            int limit
    ) {

        String sql = """
        SELECT
            id,
            title,
            description,
            address,
            status,
            created_at,

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

        WHERE status = 'APPROVED'

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
    """;

        return jdbcTemplate.query(
                sql,
                new NearbyToiletRowMapper(),

                lon,
                lat,

                lon,
                lat,
                radiusMeters,

                lon,
                lat,

                limit
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
}
