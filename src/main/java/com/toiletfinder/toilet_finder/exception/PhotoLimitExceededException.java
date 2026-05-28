package com.toiletfinder.toilet_finder.exception;

public class
PhotoLimitExceededException

        extends RuntimeException {

    public
    PhotoLimitExceededException() {

        super(
                "Maximum 2 photos allowed"
        );
    }
}
