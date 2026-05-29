package com.toiletfinder.toilet_finder.exception;

public class RateLimitExceededException
        extends RuntimeException {

    public RateLimitExceededException() {

        super("Rate limit exceeded");
    }
}