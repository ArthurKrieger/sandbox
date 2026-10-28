package dev.arthur.sandbox.counter.application;

import dev.arthur.sandbox.counter.domain.Counter;
import dev.arthur.sandbox.counter.domain.CounterId;
import dev.arthur.sandbox.counter.domain.CounterNotFoundException;
import dev.arthur.sandbox.counter.domain.CounterRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CounterApplicationServiceTest {

    private static final Instant NOW = Instant.parse("2026-10-25T12:00:00Z");

    private final Map<CounterId, Counter> stored = new HashMap<>();
    private final List<Counter> announced = new ArrayList<>();
    private final CounterApplicationService service = new CounterApplicationService(new CounterRepository() {
        @Override
        public void add(Counter counter) {
            stored.put(counter.id(), counter);
        }

        @Override
        public Optional<Counter> findById(CounterId id) {
            return Optional.ofNullable(stored.get(id));
        }
    }, announced::add, () -> "pod-p", Clock.fixed(NOW, ZoneOffset.UTC));

    @Test
    void storesTheCounterAndAnnouncesIt() {
        final Counter counter = service.create();

        assertThat(counter.createdBy()).isEqualTo("pod-p");
        assertThat(counter.createdAt()).isEqualTo(NOW);
        assertThat(stored).containsEntry(counter.id(), counter);
        assertThat(announced).containsExactly(counter);
    }

    @Test
    void givesEachCounterItsOwnId() {
        assertThat(service.create().id()).isNotEqualTo(service.create().id());
    }

    @Test
    void readsACounterBack() {
        final Counter counter = service.create();

        assertThat(service.get(counter.id())).isEqualTo(counter);
    }

    @Test
    void answersNotFoundForACounterItNeverCreated() {
        assertThatThrownBy(() -> service.get(CounterId.of(UUID.randomUUID())))
                .isInstanceOf(CounterNotFoundException.class);
    }
}
