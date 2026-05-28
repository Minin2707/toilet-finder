package com.toiletfinder.toilet_finder.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =

            LoggerFactory.getLogger(
                    GlobalExceptionHandler.class
            );

    @ExceptionHandler(
            ToiletAlreadyApprovedException.class
    )
    public ResponseEntity<ErrorResponse>
    handleToiletAlreadyApproved() {

        log.warn(
                "Toilet already approved"
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(

                        new ErrorResponse(

                                "TOILET_ALREADY_APPROVED",

                                "Toilet already approved",

                                Instant.now()
                        )
                );
    }

    @ExceptionHandler(
            UserAlreadyApprovedException.class
    )
    public ResponseEntity<ErrorResponse>
    handleUserAlreadyApproved() {

        log.warn(
                "User already approved toilet"
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(

                        new ErrorResponse(

                                "USER_ALREADY_APPROVED",

                                "User already approved this toilet",

                                Instant.now()
                        )
                );
    }

    @ExceptionHandler(
            UserAlreadyReportedException.class
    )
    public ResponseEntity<ErrorResponse>
    handleUserAlreadyReported() {

        log.warn(
                "User already reported toilet"
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(

                        new ErrorResponse(

                                "USER_ALREADY_REPORTED",

                                "User already reported this toilet",

                                Instant.now()
                        )
                );
    }

    @ExceptionHandler(
            PhotosAllowedOnlyForApprovedToiletsException.class
    )
    public ResponseEntity<ErrorResponse>
    handlePhotosAllowedOnlyForApprovedToilets() {

        log.warn(
                "Photos allowed only for approved toilets"
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(

                        new ErrorResponse(

                                "PHOTOS_ONLY_FOR_APPROVED",

                                "Photos allowed only for approved toilets",

                                Instant.now()
                        )
                );
    }

    @ExceptionHandler(
            PhotoLimitExceededException.class
    )
    public ResponseEntity<ErrorResponse>
    handlePhotoLimitExceeded() {

        log.warn(
                "Maximum photo limit exceeded"
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(

                        new ErrorResponse(

                                "PHOTO_LIMIT_EXCEEDED",

                                "Maximum 2 photos allowed",

                                Instant.now()
                        )
                );
    }
}
