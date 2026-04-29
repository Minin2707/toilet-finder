package com.toiletfinder.toilet_finder.repository;

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

    public List<Toilet> findNearby(double lat, double lon) {
        String sql = """
            SELECT
                id,
                title,
                description,
                address,
                status,
                created_at,
                ST_Y(location::geometry) as latitude,
                ST_X(location::geometry) as longitude
            FROM toilets
            WHERE status = 'APPROVED'
            ORDER BY location <-> ST_SetSRID(ST_MakePoint(?, ?), 4326)
            LIMIT 20
        """;

        return jdbcTemplate.query(
                sql,
                new ToiletRowMapper(),
                lon,
                lat
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
            ST_SetSRID(ST_MakePoint(?, ?), 4326),
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
                toilet.getLongitude(), // x
                toilet.getLatitude(),  // y
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
