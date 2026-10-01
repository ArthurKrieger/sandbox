package dev.arthur.sandbox.counter.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The counter as the producer owns it: created at zero, then finished once a consumer reports
 * the final value. Consumers do the counting; this side only records the outcome.
 */
public class Counter {

    private final CounterId id;
    private int value;
    private CounterStatus status;
    private final Instant createdAt;
    private Instant finishedAt;
    // Optimistic-locking token; null until the counter is first stored.
    private final Long version;
    private final List<CounterEvent> events = new ArrayList<>();

    private Counter(CounterId id, int value, CounterStatus status, Instant createdAt, Instant finishedAt,
            Long version) {
        this.id = Objects.requireNonNull(id);
        this.value = value;
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.finishedAt = finishedAt;
        this.version = version;
    }

    public static Counter create(Instant now) {
        Counter counter = new Counter(CounterId.generate(), 0, CounterStatus.ACTIVE, now, null, null);
        counter.events.add(new CounterEvent.CounterCreated(counter.id, counter.value));
        return counter;
    }

    public static Counter reconstitute(CounterId id, int value, CounterStatus status, Instant createdAt,
            Instant finishedAt, Long version) {
        return new Counter(id, value, status, createdAt, finishedAt, version);
    }

    /** Records the final value reported by a consumer. Repeated reports are ignored. */
    public void finish(int finalValue, Instant now) {
        if (status == CounterStatus.FINISHED) {
            return;
        }
        if (finalValue < value) {
            throw new IllegalArgumentException(
                    "final value %d is below current value %d".formatted(finalValue, value));
        }
        this.value = finalValue;
        this.status = CounterStatus.FINISHED;
        this.finishedAt = now;
    }

    /** Returns the events raised since the last call and clears them. */
    public List<CounterEvent> pullEvents() {
        List<CounterEvent> pulled = List.copyOf(events);
        events.clear();
        return pulled;
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

    public Instant createdAt() {
        return createdAt;
    }

    public Instant finishedAt() {
        return finishedAt;
    }

    public Long version() {
        return version;
    }
}
