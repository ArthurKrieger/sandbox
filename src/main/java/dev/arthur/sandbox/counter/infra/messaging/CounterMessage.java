package dev.arthur.sandbox.counter.infra.messaging;

import dev.arthur.messaging.domain.EventType;

import java.time.Instant;
import java.util.UUID;

/**
 * Payload shared by every counter.* integration event. {@code pod} and {@code occurredAt} say who
 * made the change and when, for the timeline.
 */
record CounterMessage(UUID counterId, int previousValue, int value, String pod, Instant occurredAt) {

    static final EventType CREATED = new EventType("counter", "created");
    static final EventType INCREASED = new EventType("counter", "increased");
    static final EventType FINISHED = new EventType("counter", "finished");
}
