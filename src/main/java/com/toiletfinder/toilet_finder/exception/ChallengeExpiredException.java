package com.toiletfinder.toilet_finder.exception;

public class ChallengeExpiredException
        extends RuntimeException {

    public ChallengeExpiredException() {

        super("Challenge expired");
    }
}
