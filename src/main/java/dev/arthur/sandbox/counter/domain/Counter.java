package dev.arthur.sandbox.counter.domain;

import org.jspecify.annotations.NonNull;
import org.springframework.util.Assert;

import java.time.Instant;

// What the producer decides about a counter: that it exists, and where and when it was asked for. Counting it, and
// so its value and status, belong to the consumer.
public record Counter(@NonNull CounterId id, @NonNull String createdBy, @NonNull Instant createdAt) {

    public Counter {
        Assert.notNull(id, "counter id is required");
        Assert.notNull(createdBy, "createdBy is required");
        Assert.notNull(createdAt, "createdAt is required");
    }

    public static Counter create(@NonNull String createdBy, @NonNull Instant createdAt) {
        return new Counter(CounterId.generate(), createdBy, createdAt);
    }
}
