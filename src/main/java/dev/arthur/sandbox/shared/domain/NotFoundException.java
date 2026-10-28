package dev.arthur.sandbox.shared.domain;

import org.jspecify.annotations.NonNull;

public abstract class NotFoundException extends RuntimeException {

    protected NotFoundException(@NonNull String message) {
        super(message);
    }
}
