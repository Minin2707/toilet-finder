package com.toiletfinder.toilet_finder.model;

import com.toiletfinder.toilet_finder.enumStatus.UserMessageType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserMessage {
    private UUID id;
    private UUID userId;
    private UserMessageType type;
    private String message;
    private LocalDateTime createdAt;
}