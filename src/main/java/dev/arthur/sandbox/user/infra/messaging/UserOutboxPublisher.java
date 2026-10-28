package dev.arthur.sandbox.user.infra.messaging;

import dev.arthur.messaging.domain.EventPublisher;
import dev.arthur.messaging.domain.EventType;
import dev.arthur.sandbox.user.domain.User;
import dev.arthur.sandbox.user.domain.UserEventPublisher;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
class UserOutboxPublisher implements UserEventPublisher {

    static final EventType CREATED = new EventType("user", "created");

    private final EventPublisher outbox;
    private final ObjectMapper objectMapper;

    record UserMessage(long id, String username, String email, boolean active) {
    }

    @Override
    public void created(@NonNull User user) {
        final UserMessage message = new UserMessage(user.id(), user.username(), user.email(), user.active());
        outbox.publish(CREATED, objectMapper.writeValueAsString(message));
    }
}
