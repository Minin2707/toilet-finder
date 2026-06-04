package com.toiletfinder.toilet_finder.exception;


import lombok.Getter;

@Getter
public class RateLimitExceededException
        extends RuntimeException {

    private final String code;

    private final long retryAfterSeconds;

    public RateLimitExceededException(
            String code,

            String message,

            long retryAfterSeconds
    ) {

        super(message);
        this.code = code;

        this.retryAfterSeconds =
                retryAfterSeconds;
    }

}