package dev.arthur.sandbox.counter.infra.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("counters")
record CounterRow(@Id UUID id, int value, String status, Instant createdAt, Instant finishedAt,
        @Version Long version) {
}
