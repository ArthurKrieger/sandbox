package dev.arthur.sandbox.tracking.domain;

import org.jspecify.annotations.NonNull;

public interface LocationEventLog {

    Appended append(@NonNull LocationRecorded event) throws EventLogUnavailableException;

    record Appended(int partition, long offset) {
    }
}
