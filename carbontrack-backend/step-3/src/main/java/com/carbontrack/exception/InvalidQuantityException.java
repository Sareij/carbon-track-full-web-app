package com.carbontrack.exception;

/** Thrown when a quantity is missing, negative, or above the allowed maximum. */
public class InvalidQuantityException extends RuntimeException {
    public InvalidQuantityException(String message) { super(message); }
}
