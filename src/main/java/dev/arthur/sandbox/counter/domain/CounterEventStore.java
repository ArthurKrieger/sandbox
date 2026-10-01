package dev.arthur.sandbox.counter.domain;

import java.util.List;

/** Append-only ledger of counter events; the only place counter state is stored. */
public interface CounterEventStore {

    /** A counter's events in sequence order; empty if it doesn't exist. */
    List<CounterEvent> load(CounterId id);

    /** Appends new events; fails if another pod already stored one of their sequence numbers. */
    void append(List<CounterEvent> events);
}
