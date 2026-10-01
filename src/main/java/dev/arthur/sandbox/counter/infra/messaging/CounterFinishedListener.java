package dev.arthur.sandbox.counter.infra.messaging;

import dev.arthur.messaging.domain.Event;
import dev.arthur.messaging.domain.EventConsumer;
import dev.arthur.messaging.domain.EventType;
import dev.arthur.sandbox.counter.application.CounterService;
import dev.arthur.sandbox.counter.domain.CounterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/** A consumer publishes counter.finished once the counter reaches its target. */
@Component
@RequiredArgsConstructor
class CounterFinishedListener implements EventConsumer {

    private final CounterService counterService;
    private final ObjectMapper objectMapper;

    @Override
    public boolean handles(EventType type) {
        return CounterMessage.FINISHED.equals(type);
    }

    @Override
    public void onEvent(Event event) {
        CounterMessage message = objectMapper.readValue(event.payload(), CounterMessage.class);
        counterService.finish(CounterId.of(message.counterId()), message.value());
    }
}
