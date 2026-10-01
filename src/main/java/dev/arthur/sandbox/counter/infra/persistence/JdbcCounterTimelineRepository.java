package dev.arthur.sandbox.counter.infra.persistence;

import dev.arthur.sandbox.counter.query.CounterTimeline;
import dev.arthur.sandbox.counter.query.CounterTimelineRepository;
import dev.arthur.sandbox.counter.query.TimelineEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class JdbcCounterTimelineRepository implements CounterTimelineRepository {

    // A counter is finished once any service recorded FINISHED; its value is the highest seen so far.
    private static final String SUMMARY_COLUMNS = """
            counter_id,
            CASE WHEN bool_or(type = 'FINISHED') THEN 'FINISHED' ELSE 'ACTIVE' END AS status,
            max(value) AS value,
            min(occurred_at) AS started_at,
            max(occurred_at) AS updated_at""";

    private final JdbcClient jdbc;

    @Override
    public void add(TimelineEntry entry) {
        jdbc.sql("""
                        INSERT INTO counter_timeline
                            (event_id, counter_id, service, type, previous_value, value, pod, occurred_at)
                        VALUES (:eventId, :counterId, :service, :type, :previousValue, :value, :pod, :occurredAt)
                        ON CONFLICT (event_id) DO NOTHING""")
                .param("eventId", entry.eventId())
                .param("counterId", entry.counterId())
                .param("service", entry.service())
                .param("type", entry.type())
                .param("previousValue", entry.previousValue())
                .param("value", entry.value())
                .param("pod", entry.pod())
                .param("occurredAt", Timestamp.from(entry.occurredAt()))
                .update();
    }

    @Override
    public Optional<CounterTimeline> find(UUID counterId) {
        Optional<CounterTimeline.Summary> summary = jdbc.sql(
                        "SELECT " + SUMMARY_COLUMNS + " FROM counter_timeline WHERE counter_id = :id GROUP BY counter_id")
                .param("id", counterId)
                .query((rs, row) -> toSummary(rs))
                .optional();
        return summary.map(s -> new CounterTimeline(s.counterId(), s.status(), s.value(), s.startedAt(),
                s.updatedAt(), entries(counterId)));
    }

    @Override
    public List<CounterTimeline.Summary> recent(int limit) {
        return jdbc.sql("SELECT " + SUMMARY_COLUMNS + """
                         FROM counter_timeline GROUP BY counter_id
                        ORDER BY started_at DESC LIMIT :limit""")
                .param("limit", limit)
                .query((rs, row) -> toSummary(rs))
                .list();
    }

    private List<TimelineEntry> entries(UUID counterId) {
        // Ties on the timestamp (increase and finish share one) keep the lower value first.
        return jdbc.sql("""
                        SELECT event_id, counter_id, service, type, previous_value, value, pod, occurred_at
                        FROM counter_timeline WHERE counter_id = :id
                        ORDER BY occurred_at, value, type DESC""")
                .param("id", counterId)
                .query((rs, row) -> new TimelineEntry(
                        rs.getObject("event_id", UUID.class),
                        rs.getObject("counter_id", UUID.class),
                        rs.getString("service"),
                        rs.getString("type"),
                        rs.getInt("previous_value"),
                        rs.getInt("value"),
                        rs.getString("pod"),
                        rs.getTimestamp("occurred_at").toInstant()))
                .list();
    }

    private static CounterTimeline.Summary toSummary(ResultSet rs) throws SQLException {
        return new CounterTimeline.Summary(
                rs.getObject("counter_id", UUID.class),
                rs.getString("status"),
                rs.getInt("value"),
                rs.getTimestamp("started_at").toInstant(),
                rs.getTimestamp("updated_at").toInstant());
    }
}
