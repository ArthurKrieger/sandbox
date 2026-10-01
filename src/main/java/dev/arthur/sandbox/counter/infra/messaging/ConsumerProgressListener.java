package dev.arthur.sandbox.counter.infra.messaging;

import dev.arthur.messaging.domain.Event;
import dev.arthur.messaging.domain.EventConsumer;
import dev.arthur.messaging.domain.EventType;
import dev.arthur.sandbox.counter.application.CounterService;
import dev.arthur.sandbox.counter.domain.CounterId;
import dev.arthur.sandbox.counter.query.CounterTimelineProjector;
import dev.arthur.sandbox.counter.query.TimelineEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/**
 * Consumers publish counter.increased for every step and counter.finished at the target. Every one
 * goes on the timeline; counter.finished also finishes the producer's counter.
 */
@Component
@RequiredArgsConstructor
class ConsumerProgressListener implements EventConsumer {

    private final CounterTimelineProjector timelineProjector;
    private final CounterService counterService;
    private final ObjectMapper objectMapper;

    @Override
    public boolean handles(EventType type) {
        return CounterMessage.INCREASED.equals(type) || CounterMessage.FINISHED.equals(type);
    }

    @Override
    public void onEvent(Event event) {
        CounterMessage message = objectMapper.readValue(event.payload(), CounterMessage.class);
        boolean finished = CounterMessage.FINISHED.equals(event.type());

        timelineProjector.projectConsumer(new TimelineEntry(event.id(), message.counterId(), TimelineEntry.CONSUMER,
                finished ? TimelineEntry.FINISHED : TimelineEntry.INCREASED, message.previousValue(),
                message.value(), message.pod(), message.occurredAt()));

        if (finished) {
            counterService.finish(CounterId.of(message.counterId()), message.value());
        }
    }
}
