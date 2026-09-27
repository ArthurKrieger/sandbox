package dev.arthur.sandbox.messaging;

import java.util.UUID;

public record CounterPing(UUID counterId, int count) {
}
