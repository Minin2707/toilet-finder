package com.toiletfinder.toilet_finder.exception;

public record ErrorResponse(

        String code,
        String message
) {
}
