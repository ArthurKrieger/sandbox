package dev.arthur.sandbox.counter.infra.messaging;

import dev.arthur.messaging.domain.EventPublisher;
import dev.arthur.messaging.domain.EventType;
import dev.arthur.sandbox.counter.domain.Counter;
import dev.arthur.sandbox.counter.domain.CounterEventPublisher;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

// A notice, not a copy: other services ask the producer for the counter itself.
@Component
@RequiredArgsConstructor
class CounterOutboxPublisher implements CounterEventPublisher {

    static final EventType CREATED = new EventType("counter", "created");

    private final EventPublisher outbox;
    private final ObjectMapper objectMapper;

    record CounterCreatedMessage(UUID counterId) {
    }

    @Override
    public void created(@NonNull Counter counter) {
        outbox.publish(CREATED, objectMapper.writeValueAsString(new CounterCreatedMessage(counter.id().value())));
    }
}
