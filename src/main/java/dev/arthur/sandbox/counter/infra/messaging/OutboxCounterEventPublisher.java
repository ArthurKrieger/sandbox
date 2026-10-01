package dev.arthur.sandbox.counter.infra.messaging;

import dev.arthur.messaging.domain.EventPublisher;
import dev.arthur.sandbox.counter.application.CounterEventPublisher;
import dev.arthur.sandbox.counter.domain.CounterEvent;
import dev.arthur.sandbox.counter.domain.CounterEvent.CounterCreated;
import dev.arthur.sandbox.counter.domain.CounterEvent.CounterFinished;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * Writes counter.created to the outbox; the outbox relay sends it to SNS after commit. The producer's
 * own finish is only recorded locally, since nothing downstream reacts to it.
 */
@Component
@RequiredArgsConstructor
class OutboxCounterEventPublisher implements CounterEventPublisher {

    private final EventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(List<CounterEvent> events) {
        for (CounterEvent event : events) {
            switch (event) {
                case CounterCreated created -> {
                    CounterMessage message = new CounterMessage(created.counterId().value(), created.value(),
                            created.value(), created.pod(), created.occurredAt());
                    eventPublisher.publish(CounterMessage.CREATED, objectMapper.writeValueAsString(message));
                }
                case CounterFinished finished -> {
                }
            }
        }
    }
}
