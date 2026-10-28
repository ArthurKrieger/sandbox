package dev.arthur.sandbox.tracking.domain;

import dev.arthur.sandbox.shared.domain.UnavailableException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

// The log didn't confirm the append in time, so the event may or may not have been stored.
public class EventLogUnavailableException extends UnavailableException {

    public EventLogUnavailableException(@NonNull String message, @Nullable Throwable cause) {
        super(message, cause);
    }
}
