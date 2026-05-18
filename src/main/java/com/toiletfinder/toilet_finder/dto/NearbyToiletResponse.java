package com.toiletfinder.toilet_finder.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class NearbyToiletResponse {

    private UUID id;

    private String title;

    private String description;

    private String address;

    private String status;

    private double latitude;

    private double longitude;

    private double distanceMeters;

    private String accessType;

    private Boolean wheelchairAccessible;

    private Integer confirmationCount;

    private LocalDateTime lastConfirmedAt;

    private Integer cleanCount;

    private Integer dirtyCount;

    private Integer hasPaperCount;

    private Integer warmCount;

    private Integer safeCount;
}
