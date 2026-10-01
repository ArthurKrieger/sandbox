package dev.arthur.sandbox.counter.domain;

import java.util.Optional;

public interface CounterRepository {

    Optional<Counter> findById(CounterId id);

    /** Saves the counter and returns the stored state; fails if it was changed concurrently. */
    Counter save(Counter counter);
}
