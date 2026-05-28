package com.toiletfinder.toilet_finder.exception;

public class LoginFailedException
        extends RuntimeException {

    public LoginFailedException() {

        super("Login failed");
    }
}
