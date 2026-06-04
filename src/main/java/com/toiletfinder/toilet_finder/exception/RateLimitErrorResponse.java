package com.toiletfinder.toilet_finder.exception;

import java.time.Instant;

public record RateLimitErrorResponse(

        String code,

        String message,

        long retryAfterSeconds,

        Instant timestamp
) {
}