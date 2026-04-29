package com.toiletfinder.toilet_finder.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class User {

    private UUID id;
    private String username;
    private String credentialId;
    private String publicKey;
    private LocalDateTime createdAt;
}
