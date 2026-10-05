package dev.arthur.sandbox.tracking.domain;

/** A reported location that can't be recorded as it is. */
public class InvalidLocationException extends RuntimeException {

    public InvalidLocationException(String message) {
        super(message);
    }
}
