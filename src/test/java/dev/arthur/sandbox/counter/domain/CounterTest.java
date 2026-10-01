package dev.arthur.sandbox.counter.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CounterTest {

    private static final Instant NOW = Instant.parse("2026-09-29T12:00:00Z");

    @Test
    void createStartsAtZeroAndRaisesCreatedEvent() {
        Counter counter = Counter.create(NOW);

        assertThat(counter.value()).isZero();
        assertThat(counter.status()).isEqualTo(CounterStatus.ACTIVE);
        assertThat(counter.pullEvents()).containsExactly(new CounterEvent.CounterCreated(counter.id(), 0));
        assertThat(counter.pullEvents()).isEmpty();
    }

    @Test
    void finishRecordsFinalValue() {
        Counter counter = Counter.create(NOW);

        counter.finish(3, NOW.plusSeconds(5));

        assertThat(counter.status()).isEqualTo(CounterStatus.FINISHED);
        assertThat(counter.value()).isEqualTo(3);
        assertThat(counter.finishedAt()).isEqualTo(NOW.plusSeconds(5));
    }

    @Test
    void finishIsIdempotent() {
        Counter counter = Counter.create(NOW);
        counter.finish(3, NOW);

        counter.finish(7, NOW.plusSeconds(60));

        assertThat(counter.value()).isEqualTo(3);
        assertThat(counter.finishedAt()).isEqualTo(NOW);
    }

    @Test
    void finishRejectsValueBelowCurrent() {
        Counter counter = Counter.reconstitute(CounterId.generate(), 2, CounterStatus.ACTIVE, NOW, null, 1L);

        assertThatThrownBy(() -> counter.finish(1, NOW)).isInstanceOf(IllegalArgumentException.class);
    }
}
