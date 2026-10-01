package dev.arthur.sandbox.counter.infra.messaging;

import dev.arthur.messaging.domain.EventPublisher;
import dev.arthur.messaging.domain.EventType;
import dev.arthur.sandbox.counter.application.CounterEventPublisher;
import dev.arthur.sandbox.counter.domain.CounterEvent;
import dev.arthur.sandbox.counter.domain.CounterEvent.CounterCreated;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

/** Writes counter events to the outbox; the outbox relay sends them to SNS after commit. */
@Component
@RequiredArgsConstructor
class OutboxCounterEventPublisher implements CounterEventPublisher {

    private final EventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(List<CounterEvent> events) {
        for (CounterEvent event : events) {
            switch (event) {
                case CounterCreated created -> send(CounterMessage.CREATED,
                        new CounterMessage(created.counterId().value(), created.value()));
            }
        }
    }

    private void send(EventType type, CounterMessage message) {
        eventPublisher.publish(type, objectMapper.writeValueAsString(message));
    }
}
