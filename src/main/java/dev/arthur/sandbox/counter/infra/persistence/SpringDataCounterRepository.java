package dev.arthur.sandbox.counter.infra.persistence;

import org.jspecify.annotations.NonNull;
import org.springframework.data.repository.Repository;

import java.util.Optional;
import java.util.UUID;

interface SpringDataCounterRepository extends Repository<CounterEntity, UUID> {

    Optional<CounterEntity> findById(@NonNull UUID id);
}
