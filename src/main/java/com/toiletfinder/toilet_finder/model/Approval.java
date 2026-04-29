package com.toiletfinder.toilet_finder.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Approval {

    private UUID id;
    private UUID toiletId;
    private UUID userId;
    private LocalDateTime createdAt;
}
