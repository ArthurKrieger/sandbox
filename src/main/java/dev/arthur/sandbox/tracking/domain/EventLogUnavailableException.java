package dev.arthur.sandbox.tracking.domain;

/** The event log didn't confirm an append in time, so the event may not have been stored. */
public class EventLogUnavailableException extends RuntimeException {

    public EventLogUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
