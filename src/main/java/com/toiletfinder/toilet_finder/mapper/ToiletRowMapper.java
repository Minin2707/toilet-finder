package com.toiletfinder.toilet_finder.mapper;

import com.toiletfinder.toilet_finder.model.Toilet;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ToiletRowMapper implements RowMapper<Toilet> {

    @Override
    public Toilet mapRow(ResultSet rs, int rowNum) throws SQLException {
        Toilet toilet = new Toilet();

        toilet.setId(UUID.fromString(rs.getString("id")));
        toilet.setTitle(rs.getString("title"));
        toilet.setDescription(rs.getString("description"));
        toilet.setLatitude(rs.getDouble("latitude"));
        toilet.setLongitude(rs.getDouble("longitude"));
        toilet.setAddress(rs.getString("address"));
        toilet.setStatus(rs.getString("status"));
        toilet.setAccessType(rs.getString("access_type"));
        toilet.setWheelchairAccessible(rs.getBoolean("wheelchair_accessible"));
        toilet.setCreatedAt(
                rs.getTimestamp("created_at").toLocalDateTime()
        );

        return toilet;
    }
}
