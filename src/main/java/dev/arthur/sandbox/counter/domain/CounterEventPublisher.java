package dev.arthur.sandbox.counter.domain;

import org.jspecify.annotations.NonNull;

public interface CounterEventPublisher {

    void created(@NonNull Counter counter);
}
