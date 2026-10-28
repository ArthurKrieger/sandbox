package dev.arthur.sandbox.tracking.domain;

import dev.arthur.sandbox.shared.domain.InvalidInputException;
import org.jspecify.annotations.NonNull;

public class InvalidLocationException extends InvalidInputException {

    public InvalidLocationException(@NonNull String message) {
        super(message);
    }
}
