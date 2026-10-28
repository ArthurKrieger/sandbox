package dev.arthur.sandbox.counter.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CounterTest {

    private static final Instant NOW = Instant.parse("2026-10-25T12:00:00Z");

    @Test
    void isCreatedUnderANewIdBySomeoneAtSomeTime() {
        final Counter counter = Counter.create("pod-p", NOW);

        assertThat(counter.id()).isNotNull();
        assertThat(counter.createdBy()).isEqualTo("pod-p");
        assertThat(counter.createdAt()).isEqualTo(NOW);
    }

    @Test
    void requiresEverything() {
        assertThatThrownBy(() -> new Counter(null, "pod-p", NOW)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Counter.create(null, NOW)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Counter.create("pod-p", null)).isInstanceOf(IllegalArgumentException.class);
    }
}
