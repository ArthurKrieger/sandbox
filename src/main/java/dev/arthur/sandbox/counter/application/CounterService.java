package dev.arthur.sandbox.counter.application;

import dev.arthur.sandbox.counter.domain.Counter;
import dev.arthur.sandbox.counter.domain.CounterEvent;
import dev.arthur.sandbox.counter.domain.CounterEventStore;
import dev.arthur.sandbox.counter.domain.CounterId;
import dev.arthur.sandbox.counter.domain.CounterNotFoundException;
import dev.arthur.sandbox.counter.query.CounterTimelineProjector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/** The counter's command side. Reads go through the timeline read model instead. */
@Slf4j
@Service
@RequiredArgsConstructor
public class CounterService {

    private final CounterEventStore eventStore;
    private final CounterEventPublisher eventPublisher;
    private final CounterTimelineProjector timelineProjector;
    private final PodIdentity pod;

    /** Stores the counter's creation, its counter.created event and its timeline entry in one transaction. */
    @Transactional
    public CounterId create() {
        Counter counter = Counter.create(pod.name(), Instant.now());
        record(counter.pullPendingEvents());
        log.info("counter {} created on {}", counter.id(), pod.name());
        return counter.id();
    }

    @Transactional
    public void finish(CounterId id, int finalValue) {
        List<CounterEvent> history = eventStore.load(id);
        if (history.isEmpty()) {
            throw new CounterNotFoundException(id);
        }
        Counter counter = Counter.rehydrate(id, history);
        counter.finish(finalValue, pod.name(), Instant.now());
        record(counter.pullPendingEvents());
        log.info("counter {} finished at {}", id, counter.value());
    }

    private void record(List<CounterEvent> events) {
        eventStore.append(events);
        eventPublisher.publish(events);
        timelineProjector.projectOwn(events);
    }
}
