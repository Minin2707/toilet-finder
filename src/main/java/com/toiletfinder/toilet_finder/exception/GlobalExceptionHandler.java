package com.toiletfinder.toilet_finder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
            ToiletAlreadyApprovedException.class
    )
    public ResponseEntity<ErrorResponse>
    handleToiletAlreadyApproved() {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(

                        new ErrorResponse(

                                "TOILET_ALREADY_APPROVED",

                                "Toilet already approved"
                        )
                );
    }

    @ExceptionHandler(
            UserAlreadyApprovedException.class
    )
    public ResponseEntity<ErrorResponse>
    handleUserAlreadyApproved() {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(

                        new ErrorResponse(

                                "USER_ALREADY_APPROVED",

                                "User already approved this toilet"
                        )
                );
    }
}
