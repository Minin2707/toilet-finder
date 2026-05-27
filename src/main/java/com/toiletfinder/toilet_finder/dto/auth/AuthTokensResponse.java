package com.toiletfinder.toilet_finder.dto.auth;

public record AuthTokensResponse(

        String accessToken,

        String refreshToken
) {
}