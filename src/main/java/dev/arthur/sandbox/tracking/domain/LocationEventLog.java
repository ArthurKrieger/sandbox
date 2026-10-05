package dev.arthur.sandbox.tracking.domain;

/** The tracking event store: an append-only log where each device's events stay in order. */
public interface LocationEventLog {

    /** Appends the event once it is durably stored, and says where it landed. */
    Appended append(LocationRecorded event);

    record Appended(int partition, long offset) {
    }
}
