package dev.arthur.sandbox.counter.domain;

import org.jspecify.annotations.NonNull;

import java.util.Optional;

public interface CounterRepository {

    void add(@NonNull Counter counter);

    Optional<Counter> findById(@NonNull CounterId id);
}
