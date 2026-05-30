package com.toiletfinder.toilet_finder.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    @ExceptionHandler(
            ChallengeExpiredException.class
    )
    public ResponseEntity<ErrorResponse>
    handleChallengeExpired() {

        log.warn(
                "Authentication challenge expired"
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        new ErrorResponse(
                                "CHALLENGE_EXPIRED",
                                "Challenge expired",
                                Instant.now()
                        )
                );
    }

    @ExceptionHandler(
            InvalidPasskeyException.class
    )
    public ResponseEntity<ErrorResponse>
    handleInvalidPasskey() {

        log.warn(
                "Invalid passkey authentication attempt"
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        new ErrorResponse(
                                "INVALID_PASSKEY",
                                "Invalid passkey",
                                Instant.now()
                        )
                );
    }

    @ExceptionHandler(
            LoginFailedException.class
    )
    public ResponseEntity<ErrorResponse>
    handleLoginFailed() {

        log.error(
                "Failed to process login assertion"
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        new ErrorResponse(
                                "LOGIN_FAILED",
                                "Login failed",
                                Instant.now()
                        )
                );
    }

    @ExceptionHandler(
            InvalidRefreshTokenException.class
    )
    public ResponseEntity<ErrorResponse>
    handleInvalidRefreshToken() {

        log.warn(
                "Invalid refresh token"
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(

                        new ErrorResponse(

                                "INVALID_REFRESH_TOKEN",

                                "Invalid refresh token",

                                Instant.now()
                        )
                );
    }

    @ExceptionHandler(
            PasskeyRegistrationFailedException.class
    )
    public ResponseEntity<ErrorResponse>
    handlePasskeyRegistrationFailed() {

        log.warn(
                "Passkey registration failed"
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(

                        new ErrorResponse(

                                "PASSKEY_REGISTRATION_FAILED",

                                "Passkey registration failed",

                                Instant.now()
                        )
                );
    }

    @ExceptionHandler(
            RateLimitExceededException.class
    )
    public ResponseEntity<ErrorResponse>
    handleRateLimitExceeded() {

        log.warn(
                "Rate limit exceeded"
        );

        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(

                        new ErrorResponse(

                                "RATE_LIMIT_EXCEEDED",

                                "Too many requests",

                                Instant.now()
                        )
                );
    }

    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<ErrorResponse>
    handleValidationException(
            MethodArgumentNotValidException ex
    ) {

        String message = ex
                .getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        log.warn(
                "Validation failed: {}",
                message
        );

        return ResponseEntity
                .badRequest()
                .body(

                        new ErrorResponse(

                                "VALIDATION_ERROR",

                                message,

                                Instant.now()
                        )
                );
    }

    @ExceptionHandler(
            InvalidPhotoException.class
    )
    public ResponseEntity<ErrorResponse>
    handleInvalidPhoto(
            InvalidPhotoException ex
    ) {

        log.warn(
                "Invalid photo upload: {}",
                ex.getMessage()
        );

        return ResponseEntity
                .badRequest()
                .body(

                        new ErrorResponse(

                                "INVALID_PHOTO",

                                ex.getMessage(),

                                Instant.now()
                        )
                );
    }
}
