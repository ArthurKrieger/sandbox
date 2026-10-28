package dev.arthur.sandbox.user.infra.messaging;

import dev.arthur.messaging.domain.EventPublisher;
import dev.arthur.sandbox.user.domain.User;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class UserOutboxPublisherTest {

    @Test
    void publishesTheCreatedUserAsUserCreated() {
        final ObjectMapper objectMapper = JsonMapper.builder().build();
        final EventPublisher outbox = mock(EventPublisher.class);

        new UserOutboxPublisher(outbox, objectMapper).created(new User(7L, "ana", "ana@example.com", true));

        final ArgumentCaptor<String> payload = ArgumentCaptor.forClass(String.class);
        verify(outbox).publish(eq(UserOutboxPublisher.CREATED), payload.capture());
        assertThat(UserOutboxPublisher.CREATED.value()).isEqualTo("user.created");
        final JsonNode message = objectMapper.readTree(payload.getValue());
        assertThat(message.get("id").asLong()).isEqualTo(7);
        assertThat(message.get("username").asString()).isEqualTo("ana");
        assertThat(message.get("email").asString()).isEqualTo("ana@example.com");
        assertThat(message.get("active").asBoolean()).isTrue();
    }
}
