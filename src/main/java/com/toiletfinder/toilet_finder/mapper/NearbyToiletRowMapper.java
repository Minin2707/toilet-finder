package com.toiletfinder.toilet_finder.mapper;

import com.toiletfinder.toilet_finder.dto.NearbyToiletResponse;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class NearbyToiletRowMapper
        implements RowMapper<NearbyToiletResponse> {

    @Override
    public NearbyToiletResponse mapRow(
            ResultSet rs,
            int rowNum
    ) throws SQLException {

        NearbyToiletResponse dto =
                new NearbyToiletResponse();

        dto.setId(
                UUID.fromString(rs.getString("id"))
        );

        dto.setTitle(rs.getString("title"));

        dto.setDescription(
                rs.getString("description")
        );

        dto.setAddress(
                rs.getString("address")
        );

        dto.setStatus(
                rs.getString("status")
        );

        dto.setAccessType(
                rs.getString("access_type")
        );

        dto.setWheelchairAccessible(
                rs.getBoolean(
                        "wheelchair_accessible"
                )
        );

        dto.setLatitude(
                rs.getDouble("latitude")
        );

        dto.setLongitude(
                rs.getDouble("longitude")
        );

        dto.setDistanceMeters(
                rs.getDouble("distance_meters")
        );

        return dto;
    }
}
