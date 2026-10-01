package dev.arthur.sandbox.presentation;

import dev.arthur.sandbox.counter.application.CounterService;
import dev.arthur.sandbox.counter.domain.Counter;
import dev.arthur.sandbox.counter.domain.CounterId;
import dev.arthur.sandbox.counter.domain.CounterNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/counters")
@RequiredArgsConstructor
public class CounterController {

    private final CounterService counterService;

    public record CounterResponse(UUID id, int value, String status, Instant createdAt, Instant finishedAt) {

        static CounterResponse from(Counter counter) {
            return new CounterResponse(counter.id().value(), counter.value(), counter.status().name(),
                    counter.createdAt(), counter.finishedAt());
        }
    }

    @PostMapping
    public ResponseEntity<CounterResponse> createCounter() {
        Counter counter = counterService.create();
        return ResponseEntity.created(URI.create("/api/counters/" + counter.id()))
                .body(CounterResponse.from(counter));
    }

    @GetMapping("/{id}")
    public CounterResponse getCounter(@PathVariable UUID id) {
        return CounterResponse.from(counterService.get(CounterId.of(id)));
    }

    @ExceptionHandler(CounterNotFoundException.class)
    ProblemDetail counterNotFound(CounterNotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }
}
