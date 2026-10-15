package com.carbontrack.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

/** Separate advice class so the existing handlers stay untouched. */
@RestControllerAdvice
public class ActivityExceptionHandler {

    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<ApiError> handleInvalidDateRange(InvalidDateRangeException ex) {
        HttpStatus s = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(s)
                .body(new ApiError(Instant.now(), s.value(), s.getReasonPhrase(), ex.getMessage(), null));
    }
}
