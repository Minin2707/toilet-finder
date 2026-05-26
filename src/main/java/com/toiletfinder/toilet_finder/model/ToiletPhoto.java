package com.toiletfinder.toilet_finder.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ToiletPhoto {

    private UUID id;

    private UUID toiletId;

    private UUID uploadedByUserId;

    private String photoUrl;

    private int reportCount;

    private String status;

    private LocalDateTime createdAt;
}