package com.toiletfinder.toilet_finder.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AuthChallenge {

    private UUID id;
    private String username;
    private String challenge;
    private String challengeType;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private String optionsJson;
}
