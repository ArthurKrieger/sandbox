package dev.arthur.sandbox.user.infra.config;

import dev.arthur.sandbox.user.application.UserApplicationService;
import dev.arthur.sandbox.user.domain.UserEventPublisher;
import dev.arthur.sandbox.user.domain.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration(proxyBeanMethods = false)
class UserConfiguration {

    @Bean
    UserApplicationService userApplicationService(@NonNull UserRepository users,
            @NonNull UserEventPublisher eventPublisher, @NonNull Clock clock) {
        return new UserApplicationService(users, eventPublisher, clock);
    }
}
