package dev.arthur.sandbox.counter.query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Storage for the timeline read model. Separate from the ledger, and safe to rebuild from events. */
public interface CounterTimelineRepository {

    /** Adds an entry; an entry whose event id is already stored is ignored. */
    void add(TimelineEntry entry);

    Optional<CounterTimeline> find(UUID counterId);

    /** The most recently started counters, newest first. */
    List<CounterTimeline.Summary> recent(int limit);
}
