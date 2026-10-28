package dev.arthur.sandbox.counter.infra.persistence;

import dev.arthur.sandbox.counter.domain.Counter;
import dev.arthur.sandbox.counter.domain.CounterId;
import dev.arthur.sandbox.counter.domain.CounterRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.stereotype.Component;

import java.util.Optional;

// insert, not save(): the id is assigned, so save() would try an update.
@Component
@RequiredArgsConstructor
class JdbcCounterRepository implements CounterRepository {

    private final SpringDataCounterRepository rows;
    private final JdbcAggregateOperations aggregates;

    @Override
    public void add(@NonNull Counter counter) {
        aggregates.insert(new CounterEntity(counter.id().value(), counter.createdBy(), counter.createdAt()));
    }

    @Override
    public Optional<Counter> findById(@NonNull CounterId id) {
        return rows.findById(id.value()).map(row -> new Counter(id, row.createdBy(), row.createdAt()));
    }
}
