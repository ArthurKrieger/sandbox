package dev.arthur.sandbox.counter.domain;

import dev.arthur.sandbox.counter.domain.CounterEvent.CounterCreated;
import dev.arthur.sandbox.counter.domain.CounterEvent.CounterFinished;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CounterTest {

    private static final Instant NOW = Instant.parse("2026-09-29T12:00:00Z");

    @Test
    void createStartsAtZeroAndRecordsCreation() {
        Counter counter = Counter.create("pod-p", NOW);

        assertThat(counter.value()).isZero();
        assertThat(counter.status()).isEqualTo(CounterStatus.ACTIVE);
        assertThat(counter.pullPendingEvents()).containsExactly(new CounterCreated(counter.id(), 1, 0, "pod-p", NOW));
        assertThat(counter.pullPendingEvents()).isEmpty();
    }

    @Test
    void finishRecordsFinalValue() {
        CounterId id = CounterId.generate();
        Counter counter = Counter.rehydrate(id, List.of(new CounterCreated(id, 1, 0, "pod-p", NOW)));

        counter.finish(3, "pod-q", NOW.plusSeconds(5));

        assertThat(counter.status()).isEqualTo(CounterStatus.FINISHED);
        assertThat(counter.value()).isEqualTo(3);
        assertThat(counter.pullPendingEvents())
                .containsExactly(new CounterFinished(id, 2, 3, "pod-q", NOW.plusSeconds(5)));
    }

    @Test
    void finishIsIdempotent() {
        CounterId id = CounterId.generate();
        Counter counter = Counter.rehydrate(id, List.of(
                new CounterCreated(id, 1, 0, "pod-p", NOW),
                new CounterFinished(id, 2, 3, "pod-p", NOW)));

        counter.finish(7, "pod-p", NOW.plusSeconds(60));

        assertThat(counter.value()).isEqualTo(3);
        assertThat(counter.pullPendingEvents()).isEmpty();
    }

    @Test
    void historyMustStartWithCreation() {
        CounterId id = CounterId.generate();

        assertThatThrownBy(() -> Counter.rehydrate(id, List.of(new CounterFinished(id, 1, 3, "pod-p", NOW))))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
