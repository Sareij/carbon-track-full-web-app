package com.carbontrack.exception;

/** Thrown when no active emission factor exists for the requested activity type + unit. */
public class UnknownActivityException extends RuntimeException {
    public UnknownActivityException(String message) { super(message); }
}
