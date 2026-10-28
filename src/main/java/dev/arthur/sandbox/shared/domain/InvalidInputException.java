package dev.arthur.sandbox.shared.domain;

import org.jspecify.annotations.NonNull;

public abstract class InvalidInputException extends RuntimeException {

    protected InvalidInputException(@NonNull String message) {
        super(message);
    }
}
