package dev.arthur.sandbox.counter.query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Read model for one counter: its current state plus every change in the order it happened. */
public record CounterTimeline(UUID counterId, String status, int value, Instant startedAt, Instant updatedAt,
        List<TimelineEntry> entries) {

    /** A counter in the recent list, without its entries. */
    public record Summary(UUID counterId, String status, int value, Instant startedAt, Instant updatedAt) {
    }
}
