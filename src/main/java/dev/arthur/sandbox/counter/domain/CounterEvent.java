package dev.arthur.sandbox.counter.domain;

import java.time.Instant;

/**
 * An entry in the producer's ledger for a counter, numbered 1, 2... by {@code sequence}.
 * {@code pod} is the task that made the change.
 */
public sealed interface CounterEvent permits CounterEvent.CounterCreated, CounterEvent.CounterFinished {

    CounterId counterId();

    int sequence();

    int value();

    String pod();

    Instant occurredAt();

    record CounterCreated(CounterId counterId, int sequence, int value, String pod, Instant occurredAt)
            implements CounterEvent {
    }

    /** The producer recorded the final value a consumer reported. */
    record CounterFinished(CounterId counterId, int sequence, int value, String pod, Instant occurredAt)
            implements CounterEvent {
    }
}
