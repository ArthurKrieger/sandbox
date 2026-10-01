package dev.arthur.sandbox.counter.infra.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

interface CounterRowRepository extends CrudRepository<CounterRow, UUID> {
}
