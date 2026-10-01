package dev.arthur.sandbox.counter.infra.messaging;

import dev.arthur.messaging.domain.EventType;

import java.util.UUID;

/** Payload shared by every counter.* integration event. */
record CounterMessage(UUID counterId, int value) {

    static final EventType CREATED = new EventType("counter", "created");
    static final EventType FINISHED = new EventType("counter", "finished");
}
