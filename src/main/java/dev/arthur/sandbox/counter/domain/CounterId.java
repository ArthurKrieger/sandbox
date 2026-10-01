package dev.arthur.sandbox.counter.domain;

import java.util.Objects;
import java.util.UUID;

public record CounterId(UUID value) {

    public CounterId {
        Objects.requireNonNull(value, "counter id is required");
    }

    public static CounterId generate() {
        return new CounterId(UUID.randomUUID());
    }

    public static CounterId of(UUID value) {
        return new CounterId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
