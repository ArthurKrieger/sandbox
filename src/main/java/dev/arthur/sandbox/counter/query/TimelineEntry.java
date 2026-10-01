package dev.arthur.sandbox.counter.query;

import java.time.Instant;
import java.util.UUID;

/**
 * One change to a counter as the timeline shows it: what happened, in which service and pod, and when.
 * {@code eventId} identifies the event it was projected from, so replays don't add it twice.
 */
public record TimelineEntry(UUID eventId, UUID counterId, String service, String type, int previousValue,
        int value, String pod, Instant occurredAt) {

    public static final String PRODUCER = "producer";
    public static final String CONSUMER = "consumer";

    public static final String CREATED = "CREATED";
    public static final String INCREASED = "INCREASED";
    public static final String FINISHED = "FINISHED";
}
