package dev.arthur.sandbox.messaging;

import dev.arthur.messaging.domain.EventType;

import java.util.UUID;

public record CounterPing(UUID counterId, int count) {

    public static final EventType TYPE = new EventType("counter", "pinged");
}
