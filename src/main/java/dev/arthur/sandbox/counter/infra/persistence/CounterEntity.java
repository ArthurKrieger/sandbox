package dev.arthur.sandbox.counter.infra.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("counters")
record CounterEntity(@Id UUID id, String createdBy, Instant createdAt) {
}
