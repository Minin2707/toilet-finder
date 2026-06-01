package com.toiletfinder.toilet_finder.dto.auth;

import com.toiletfinder.toilet_finder.model.RefreshToken;

public record RefreshTokenResult(

        String rawToken,

        RefreshToken refreshToken
) {
}