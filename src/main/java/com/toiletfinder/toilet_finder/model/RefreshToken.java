package com.toiletfinder.toilet_finder.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class RefreshToken {

    private UUID id;

    private UUID userId;

    private String tokenHash;

    private LocalDateTime expiresAt;

    private boolean revoked;

    private LocalDateTime createdAt;
}