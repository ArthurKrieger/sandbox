package dev.arthur.sandbox.counter.infra.persistence;

import dev.arthur.sandbox.counter.domain.Counter;
import dev.arthur.sandbox.counter.domain.CounterId;
import dev.arthur.sandbox.counter.domain.CounterRepository;
import dev.arthur.sandbox.counter.domain.CounterStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class JdbcCounterRepository implements CounterRepository {

    private final CounterRowRepository rows;

    @Override
    public Optional<Counter> findById(CounterId id) {
        return rows.findById(id.value()).map(JdbcCounterRepository::toDomain);
    }

    @Override
    public Counter save(Counter counter) {
        // A stale version makes Spring Data JDBC throw OptimisticLockingFailureException.
        return toDomain(rows.save(toRow(counter)));
    }

    private static CounterRow toRow(Counter counter) {
        return new CounterRow(counter.id().value(), counter.value(), counter.status().name(),
                counter.createdAt(), counter.finishedAt(), counter.version());
    }

    private static Counter toDomain(CounterRow row) {
        return Counter.reconstitute(CounterId.of(row.id()), row.value(), CounterStatus.valueOf(row.status()),
                row.createdAt(), row.finishedAt(), row.version());
    }
}
