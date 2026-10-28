package dev.arthur.sandbox.shared.domain;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public abstract class UnavailableException extends RuntimeException {

    protected UnavailableException(@NonNull String message, @Nullable Throwable cause) {
        super(message, cause);
    }
}
