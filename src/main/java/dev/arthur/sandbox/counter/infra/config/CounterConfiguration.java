package dev.arthur.sandbox.counter.infra.config;

import dev.arthur.sandbox.counter.application.CounterApplicationService;
import dev.arthur.sandbox.counter.domain.CounterEventPublisher;
import dev.arthur.sandbox.counter.domain.CounterRepository;
import dev.arthur.sandbox.shared.application.port.InstanceIdentifierResolver;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration(proxyBeanMethods = false)
class CounterConfiguration {

    @Bean
    CounterApplicationService counterApplicationService(@NonNull CounterRepository counters,
            @NonNull CounterEventPublisher eventPublisher, @NonNull InstanceIdentifierResolver identifierResolver,
            @NonNull Clock clock) {
        return new CounterApplicationService(counters, eventPublisher, identifierResolver, clock);
    }
}
