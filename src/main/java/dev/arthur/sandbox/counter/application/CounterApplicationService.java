package dev.arthur.sandbox.counter.application;

import dev.arthur.sandbox.counter.domain.Counter;
import dev.arthur.sandbox.counter.domain.CounterEventPublisher;
import dev.arthur.sandbox.counter.domain.CounterId;
import dev.arthur.sandbox.counter.domain.CounterNotFoundException;
import dev.arthur.sandbox.counter.domain.CounterRepository;
import dev.arthur.sandbox.shared.application.port.InstanceIdentifierResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Slf4j
@RequiredArgsConstructor
public class CounterApplicationService {

    private final CounterRepository counters;
    private final CounterEventPublisher eventPublisher;
    private final InstanceIdentifierResolver identifierResolver;
    private final Clock clock;

    // Stored and announced in one transaction: the consumer never hears of a counter it can't look up.
    @Transactional
    public Counter create() {
        final Counter counter = Counter.create(identifierResolver.resolve(), clock.instant());
        counters.add(counter);
        eventPublisher.created(counter);
        log.info("counter {} created", counter.id());
        return counter;
    }

    @Transactional(readOnly = true)
    public Counter get(@NonNull CounterId id) {
        return counters.findById(id).orElseThrow(() -> new CounterNotFoundException(id));
    }
}
