package dev.arthur.sandbox.counter.domain;

public class CounterNotFoundException extends RuntimeException {

    public CounterNotFoundException(CounterId id) {
        super("counter not found: " + id);
    }
}
