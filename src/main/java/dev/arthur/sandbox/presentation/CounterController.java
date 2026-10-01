package dev.arthur.sandbox.presentation;

import dev.arthur.sandbox.counter.application.CounterService;
import dev.arthur.sandbox.counter.domain.CounterId;
import dev.arthur.sandbox.counter.query.CounterTimeline;
import dev.arthur.sandbox.counter.query.CounterTimelineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/** Commands go to the counter's event-sourced ledger; queries read the timeline projection. */
@RestController
@RequestMapping("/api/counters")
@RequiredArgsConstructor
public class CounterController {

    private static final int MAX_RECENT = 50;

    private final CounterService counterService;
    private final CounterTimelineRepository timeline;

    public record CreatedCounter(UUID id) {
    }

    @PostMapping
    public ResponseEntity<CreatedCounter> createCounter() {
        CounterId id = counterService.create();
        return ResponseEntity.created(URI.create("/api/counters/" + id)).body(new CreatedCounter(id.value()));
    }

    @GetMapping
    public List<CounterTimeline.Summary> recentCounters(@RequestParam(defaultValue = "20") int limit) {
        return timeline.recent(Math.clamp(limit, 1, MAX_RECENT));
    }

    @GetMapping("/{id}")
    public CounterTimeline getCounter(@PathVariable UUID id) {
        return timeline.find(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "counter not found: " + id));
    }
}
