package dev.arthur.sandbox.counter.presentation.web;

import dev.arthur.sandbox.counter.application.CounterApplicationService;
import dev.arthur.sandbox.counter.domain.Counter;
import dev.arthur.sandbox.counter.domain.CounterId;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

// Reads of a counter's progress go to the consumer, which counts it; this only creates counters and says who made
// each one.
@RestController
@RequestMapping("/api/counters")
@RequiredArgsConstructor
public class CounterController {

    private final CounterApplicationService counters;

    public record CreatedCounter(UUID id) {
    }

    public record CounterResponse(UUID id, String createdBy, Instant createdAt) {

        static CounterResponse from(@NonNull Counter counter) {
            return new CounterResponse(counter.id().value(), counter.createdBy(), counter.createdAt());
        }
    }

    @PostMapping
    public ResponseEntity<CreatedCounter> createCounter() {
        final Counter counter = counters.create();
        return ResponseEntity.created(URI.create("/api/counters/" + counter.id()))
                .body(new CreatedCounter(counter.id().value()));
    }

    @GetMapping("/{id}")
    public CounterResponse getCounter(@PathVariable @NonNull UUID id) {
        return CounterResponse.from(counters.get(CounterId.of(id)));
    }
}
