package com.toiletfinder.toilet_finder.service;

import com.toiletfinder.toilet_finder.dto.Coordinates;
import com.toiletfinder.toilet_finder.enumStatus.ToiletSource;
import com.toiletfinder.toilet_finder.model.Toilet;
import com.toiletfinder.toilet_finder.repository.ToiletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OsmImportService {

    @Value("${osm.import.file-path}")
    private String filePath;

    private final ObjectMapper objectMapper;

    private final ToiletRepository toiletRepository;

    public void importGeoJson() {

        try {

            JsonNode root =
                    objectMapper.readTree(
                            new File(filePath)
                    );

            JsonNode features =
                    root.get("features");

            int imported = 0;

            for (JsonNode feature : features) {

                Coordinates coordinates =
                        extractCoordinates(

                                feature.get(
                                        "geometry"
                                )
                        );

                double longitude =
                        coordinates.longitude();

                double latitude =
                        coordinates.latitude();

                Toilet toilet =
                        new Toilet();

                toilet.setId(
                        UUID.randomUUID()
                );

                toilet.setTitle(
                        "Public Toilet"
                );

                toilet.setLatitude(
                        latitude
                );

                toilet.setLongitude(
                        longitude
                );

                toilet.setStatus(
                        "APPROVED"
                );

                toilet.setCreatedAt(
                        LocalDateTime.now()
                );

                toilet.setToiletSource(
                        ToiletSource.OSM
                );

                toiletRepository.save(
                        toilet
                );

                imported++;
            }

            log.info(
                    "Imported {} OSM toilets",
                    imported
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to import OSM toilets",
                    e
            );
        }
    }

    private Coordinates extractCoordinates(
            JsonNode geometry
    ) {

        String geometryType =
                geometry
                        .get("type")
                        .asText();

        JsonNode coordinates =
                geometry
                        .get("coordinates");

        return switch (geometryType) {

            case "Point" ->

                    new Coordinates(

                            coordinates
                                    .get(0)
                                    .asDouble(),

                            coordinates
                                    .get(1)
                                    .asDouble()
                    );

            case "LineString" ->

                    new Coordinates(

                            coordinates
                                    .get(0)
                                    .get(0)
                                    .asDouble(),

                            coordinates
                                    .get(0)
                                    .get(1)
                                    .asDouble()
                    );

            case "Polygon" ->

                    new Coordinates(

                            coordinates
                                    .get(0)
                                    .get(0)
                                    .get(0)
                                    .asDouble(),

                            coordinates
                                    .get(0)
                                    .get(0)
                                    .get(1)
                                    .asDouble()
                    );

            case "MultiPolygon" ->

                    new Coordinates(

                            coordinates
                                    .get(0)
                                    .get(0)
                                    .get(0)
                                    .get(0)
                                    .asDouble(),

                            coordinates
                                    .get(0)
                                    .get(0)
                                    .get(0)
                                    .get(1)
                                    .asDouble()
                    );

            default -> throw new IllegalArgumentException(

                    "Unsupported geometry type: "
                            + geometryType
            );
        };
    }
}
