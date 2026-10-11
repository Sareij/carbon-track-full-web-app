package com.carbontrack.security;

/** Thrown when a JWT is malformed, forged, uses the wrong algorithm, or has expired. */
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) { super(message); }
}
