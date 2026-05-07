package com.toiletfinder.toilet_finder.dto;

import lombok.Data;

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
}
