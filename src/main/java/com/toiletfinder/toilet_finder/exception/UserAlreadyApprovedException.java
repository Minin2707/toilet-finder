package com.toiletfinder.toilet_finder.exception;

public class UserAlreadyApprovedException
        extends RuntimeException {

    public UserAlreadyApprovedException() {

        super("User already approved this toilet");
    }
}
