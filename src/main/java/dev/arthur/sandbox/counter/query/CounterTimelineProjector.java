package dev.arthur.sandbox.counter.query;

import dev.arthur.sandbox.counter.domain.CounterEvent;
import dev.arthur.sandbox.counter.domain.CounterEvent.CounterCreated;
import dev.arthur.sandbox.counter.domain.CounterEvent.CounterFinished;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Builds the timeline from every counter event: the producer's own, in the transaction that records
 * them, and the consumers', as they arrive over SQS.
 */
@Component
@RequiredArgsConstructor
public class CounterTimelineProjector {

    private final CounterTimelineRepository timeline;

    public void projectOwn(List<CounterEvent> events) {
        for (CounterEvent event : events) {
            String type = switch (event) {
                case CounterCreated created -> TimelineEntry.CREATED;
                case CounterFinished finished -> TimelineEntry.FINISHED;
            };
            timeline.add(new TimelineEntry(UUID.randomUUID(), event.counterId().value(), TimelineEntry.PRODUCER,
                    type, event.value(), event.value(), event.pod(), event.occurredAt()));
        }
    }

    public void projectConsumer(TimelineEntry entry) {
        timeline.add(entry);
    }
}
