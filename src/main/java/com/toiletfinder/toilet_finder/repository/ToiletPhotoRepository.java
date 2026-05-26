package com.toiletfinder.toilet_finder.repository;

import com.toiletfinder.toilet_finder.dto.ToiletPhotoResponse;
import com.toiletfinder.toilet_finder.model.ToiletPhoto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ToiletPhotoRepository {

    private final JdbcTemplate jdbcTemplate;

    public void save(
            ToiletPhoto photo
    ) {

        String sql = """
            INSERT INTO toilet_photos (
                id,
                toilet_id,
                uploaded_by_user_id,
                photo_url,
                report_count,
                status,
                created_at
            )
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(

                sql,

                photo.getId(),

                photo.getToiletId(),

                photo.getUploadedByUserId(),

                photo.getPhotoUrl(),

                photo.getReportCount(),

                photo.getStatus(),

                photo.getCreatedAt()
        );
    }

    public int countActivePhotos(
            UUID toiletId
    ) {

        String sql = """
            SELECT COUNT(*)
            FROM toilet_photos
            WHERE toilet_id = ?
            AND status = 'ACTIVE'
        """;

        Integer count =
                jdbcTemplate.queryForObject(

                        sql,

                        Integer.class,

                        toiletId
                );

        return count == null ? 0 : count;
    }

    public List<ToiletPhotoResponse> findActiveByToiletId(
            UUID toiletId
    ) {

        String sql = """
        SELECT
            id,
            photo_url
        FROM toilet_photos
        WHERE toilet_id = ?
        AND status = 'ACTIVE'
        ORDER BY created_at DESC
    """;

        return jdbcTemplate.query(

                sql,

                (rs, rowNum) -> new ToiletPhotoResponse(

                        UUID.fromString(
                                rs.getString("id")
                        ),

                        rs.getString("photo_url")
                ),

                toiletId
        );
    }
}