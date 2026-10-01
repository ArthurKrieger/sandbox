package dev.arthur.sandbox.counter.domain;

public sealed interface CounterEvent permits CounterEvent.CounterCreated {

    CounterId counterId();

    record CounterCreated(CounterId counterId, int value) implements CounterEvent {
    }
}
