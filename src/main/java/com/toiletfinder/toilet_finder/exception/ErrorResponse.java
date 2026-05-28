package com.toiletfinder.toilet_finder.exception;

import java.time.Instant;

public record ErrorResponse(

        String code,
        String message,
        Instant timestamp
) {
}
