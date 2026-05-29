package com.toiletfinder.toilet_finder.exception;

public class InvalidRefreshTokenException
        extends RuntimeException {

    public InvalidRefreshTokenException(
            String message
    ) {
        super(message);
    }
}
