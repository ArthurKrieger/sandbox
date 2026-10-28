package dev.arthur.sandbox.counter.domain;

import org.jspecify.annotations.NonNull;
import org.springframework.util.Assert;

import java.util.UUID;

public record CounterId(@NonNull UUID value) {

    public CounterId {
        Assert.notNull(value, "counter id is required");
    }

    public static CounterId generate() {
        return new CounterId(UUID.randomUUID());
    }

    public static CounterId of(@NonNull UUID value) {
        return new CounterId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
