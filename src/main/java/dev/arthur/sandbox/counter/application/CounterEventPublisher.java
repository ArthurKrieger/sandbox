package dev.arthur.sandbox.counter.application;

import dev.arthur.sandbox.counter.domain.CounterEvent;

import java.util.List;

/** Hands domain events to other services; must join the caller's transaction. */
public interface CounterEventPublisher {

    void publish(List<CounterEvent> events);
}
