package dev.arthur.sandbox.messaging;

import dev.arthur.messaging.domain.EventPublisher;
import dev.arthur.messaging.domain.EventConsumer;
import dev.arthur.messaging.domain.EventType;
import dev.arthur.messaging.domain.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class CounterPingHandler implements EventConsumer {

    private static final EventType TYPE = new EventType("counter", "pinged");
    private static final int STOP_AT = 3;

    private final EventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    @Override
    public boolean handles(EventType type) {
        return TYPE.equals(type);
    }

    @Override
    public void onEvent(Event event) {
        CounterPing ping = objectMapper.readValue(event.payload(), CounterPing.class);
        int next = ping.count() + 1;

        log.info("counter {} incremented to {} (producer)", ping.counterId(), next);

        if (next >= STOP_AT) {
            log.info("counter {} reached {}, stopping", ping.counterId(), next);
            return;
        }

        CounterPing reply = new CounterPing(ping.counterId(), next);
        eventPublisher.publish(TYPE, objectMapper.writeValueAsString(reply));
    }
}
