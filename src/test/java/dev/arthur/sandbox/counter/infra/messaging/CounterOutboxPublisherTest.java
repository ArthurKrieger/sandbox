package dev.arthur.sandbox.counter.infra.messaging;

import dev.arthur.messaging.domain.EventPublisher;
import dev.arthur.sandbox.counter.domain.Counter;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CounterOutboxPublisherTest {

    @Test
    void announcesTheCounterByIdOnly() {
        final ObjectMapper objectMapper = JsonMapper.builder().build();
        final EventPublisher outbox = mock(EventPublisher.class);
        final Counter counter = Counter.create("pod-p", Instant.parse("2026-10-25T12:00:00Z"));

        new CounterOutboxPublisher(outbox, objectMapper).created(counter);

        final ArgumentCaptor<String> payload = ArgumentCaptor.forClass(String.class);
        verify(outbox).publish(eq(CounterOutboxPublisher.CREATED), payload.capture());
        assertThat(CounterOutboxPublisher.CREATED.value()).isEqualTo("counter.created");
        final JsonNode message = objectMapper.readTree(payload.getValue());
        assertThat(message.propertyNames()).containsExactly("counterId");
        assertThat(message.get("counterId").asString()).isEqualTo(counter.id().toString());
    }
}
