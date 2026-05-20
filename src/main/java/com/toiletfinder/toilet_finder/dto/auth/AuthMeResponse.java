package com.toiletfinder.toilet_finder.dto.auth;

import java.util.UUID;

public record AuthMeResponse(

        UUID userId,

        boolean authenticated
) {
}
