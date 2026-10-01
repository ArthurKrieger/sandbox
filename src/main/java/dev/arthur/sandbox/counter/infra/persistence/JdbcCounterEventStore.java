package dev.arthur.sandbox.counter.infra.persistence;

import dev.arthur.sandbox.counter.domain.CounterEvent;
import dev.arthur.sandbox.counter.domain.CounterEvent.CounterCreated;
import dev.arthur.sandbox.counter.domain.CounterEvent.CounterFinished;
import dev.arthur.sandbox.counter.domain.CounterEventStore;
import dev.arthur.sandbox.counter.domain.CounterId;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
class JdbcCounterEventStore implements CounterEventStore {

    private static final String CREATED = "CREATED";
    private static final String FINISHED = "FINISHED";

    private final JdbcClient jdbc;

    @Override
    public List<CounterEvent> load(CounterId id) {
        return jdbc.sql("""
                        SELECT sequence, type, value, pod, occurred_at
                        FROM counter_events WHERE counter_id = :id ORDER BY sequence""")
                .param("id", id.value())
                .query((rs, row) -> toEvent(id, rs))
                .list();
    }

    @Override
    public void append(List<CounterEvent> events) {
        // The (counter_id, sequence) primary key rejects a concurrent writer with DuplicateKeyException.
        for (CounterEvent event : events) {
            jdbc.sql("""
                            INSERT INTO counter_events (counter_id, sequence, type, value, pod, occurred_at)
                            VALUES (:counterId, :sequence, :type, :value, :pod, :occurredAt)""")
                    .param("counterId", event.counterId().value())
                    .param("sequence", event.sequence())
                    .param("type", typeOf(event))
                    .param("value", event.value())
                    .param("pod", event.pod())
                    .param("occurredAt", Timestamp.from(event.occurredAt()))
                    .update();
        }
    }

    private static String typeOf(CounterEvent event) {
        return switch (event) {
            case CounterCreated created -> CREATED;
            case CounterFinished finished -> FINISHED;
        };
    }

    private static CounterEvent toEvent(CounterId id, ResultSet rs) throws SQLException {
        int sequence = rs.getInt("sequence");
        int value = rs.getInt("value");
        String pod = rs.getString("pod");
        Instant occurredAt = rs.getTimestamp("occurred_at").toInstant();
        return switch (rs.getString("type")) {
            case CREATED -> new CounterCreated(id, sequence, value, pod, occurredAt);
            case FINISHED -> new CounterFinished(id, sequence, value, pod, occurredAt);
            default -> throw new IllegalStateException("unknown counter event type: " + rs.getString("type"));
        };
    }
}
