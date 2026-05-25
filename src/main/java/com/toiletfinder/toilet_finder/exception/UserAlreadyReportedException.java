package com.toiletfinder.toilet_finder.exception;

public class UserAlreadyReportedException
        extends RuntimeException {

    public UserAlreadyReportedException() {

        super("User already reported this toilet");
    }
}