package com.carbontrack.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

/** Turns the emission engine's exceptions into clean 400 responses. */
@RestControllerAdvice
public class EmissionExceptionHandler {

    @ExceptionHandler(UnknownActivityException.class)
    public ResponseEntity<ApiError> handleUnknownActivity(UnknownActivityException ex) {
        return badRequest(ex.getMessage());
    }

    @ExceptionHandler(InvalidQuantityException.class)
    public ResponseEntity<ApiError> handleInvalidQuantity(InvalidQuantityException ex) {
        return badRequest(ex.getMessage());
    }

    private ResponseEntity<ApiError> badRequest(String message) {
        HttpStatus s = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(s).body(new ApiError(Instant.now(), s.value(), s.getReasonPhrase(), message, null));
    }
}
