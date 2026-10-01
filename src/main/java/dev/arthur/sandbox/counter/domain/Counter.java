package dev.arthur.sandbox.counter.domain;

import dev.arthur.sandbox.counter.domain.CounterEvent.CounterCreated;
import dev.arthur.sandbox.counter.domain.CounterEvent.CounterFinished;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The counter as the producer owns it, rebuilt from its ledger: created at zero, then finished once a
 * consumer reports the final value. Consumers do the counting; this side only records the outcome.
 */
public class Counter {

    private final CounterId id;
    private int value;
    private CounterStatus status;
    private int sequence;
    private final List<CounterEvent> pendingEvents = new ArrayList<>();

    private Counter(CounterId id) {
        this.id = Objects.requireNonNull(id);
    }

    public static Counter create(String pod, Instant now) {
        Counter counter = new Counter(CounterId.generate());
        counter.record(new CounterCreated(counter.id, 1, 0, pod, now));
        return counter;
    }

    /** Replays a counter's ledger; {@code history} must start with its creation. */
    public static Counter rehydrate(CounterId id, List<CounterEvent> history) {
        if (history.isEmpty() || !(history.getFirst() instanceof CounterCreated)) {
            throw new IllegalArgumentException("counter " + id + " history must start with its creation");
        }
        Counter counter = new Counter(id);
        history.forEach(counter::apply);
        return counter;
    }

    /** Records the final value reported by a consumer. Repeated reports are ignored. */
    public void finish(int finalValue, String pod, Instant now) {
        if (status == CounterStatus.FINISHED) {
            return;
        }
        if (finalValue < value) {
            throw new IllegalArgumentException(
                    "final value %d is below current value %d".formatted(finalValue, value));
        }
        record(new CounterFinished(id, sequence + 1, finalValue, pod, now));
    }

    /** Returns the events recorded since the counter was created or loaded and clears them. */
    public List<CounterEvent> pullPendingEvents() {
        List<CounterEvent> pulled = List.copyOf(pendingEvents);
        pendingEvents.clear();
        return pulled;
    }

    private void record(CounterEvent event) {
        apply(event);
        pendingEvents.add(event);
    }

    private void apply(CounterEvent event) {
        switch (event) {
            case CounterCreated created -> status = CounterStatus.ACTIVE;
            case CounterFinished finished -> status = CounterStatus.FINISHED;
        }
        value = event.value();
        sequence = event.sequence();
    }

    public CounterId id() {
        return id;
    }

    public int value() {
        return value;
    }

    public CounterStatus status() {
        return status;
    }
}
