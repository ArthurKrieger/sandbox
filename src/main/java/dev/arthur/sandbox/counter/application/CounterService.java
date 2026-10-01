package dev.arthur.sandbox.counter.application;

import dev.arthur.sandbox.counter.domain.Counter;
import dev.arthur.sandbox.counter.domain.CounterEvent;
import dev.arthur.sandbox.counter.domain.CounterId;
import dev.arthur.sandbox.counter.domain.CounterNotFoundException;
import dev.arthur.sandbox.counter.domain.CounterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CounterService {

    private final CounterRepository counters;
    private final CounterEventPublisher eventPublisher;

    /** Stores the counter and its counter.created event in the same transaction (outbox). */
    @Transactional
    public Counter create() {
        Counter counter = Counter.create(Instant.now());
        List<CounterEvent> events = counter.pullEvents();
        Counter saved = counters.save(counter);
        eventPublisher.publish(events);
        log.info("counter {} created", saved.id());
        return saved;
    }

    @Transactional
    public void finish(CounterId id, int finalValue) {
        Counter counter = get(id);
        counter.finish(finalValue, Instant.now());
        counters.save(counter);
        log.info("counter {} finished at {}", id, counter.value());
    }

    @Transactional(readOnly = true)
    public Counter get(CounterId id) {
        return counters.findById(id).orElseThrow(() -> new CounterNotFoundException(id));
    }
}
