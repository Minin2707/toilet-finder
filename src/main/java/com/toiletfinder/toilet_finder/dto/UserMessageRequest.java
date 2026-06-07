package com.toiletfinder.toilet_finder.dto;

import com.toiletfinder.toilet_finder.enumStatus.UserMessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserMessageRequest(
        @NotNull
        UserMessageType type,
        @NotBlank
        @Size(max = 2000)
        String message
) {
}
