package com.toiletfinder.toilet_finder.exception;

public class InvalidPasskeyException
        extends RuntimeException {

    public InvalidPasskeyException() {

        super("Invalid passkey");
    }
}
