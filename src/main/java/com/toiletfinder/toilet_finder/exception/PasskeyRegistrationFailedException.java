package com.toiletfinder.toilet_finder.exception;

public class PasskeyRegistrationFailedException
        extends RuntimeException {

    public PasskeyRegistrationFailedException() {

        super("Passkey registration failed");
    }
}