package com.toiletfinder.toilet_finder.exception;

public class PhotoStorageException
        extends RuntimeException {

    public PhotoStorageException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}
