package com.toiletfinder.toilet_finder.exception;

public class ToiletAlreadyApprovedException
        extends RuntimeException {

    public ToiletAlreadyApprovedException() {

        super("Toilet already approved");
    }
}
