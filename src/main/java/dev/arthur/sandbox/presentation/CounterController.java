package dev.arthur.sandbox.presentation;

import dev.arthur.messaging.domain.EventPublisher;
import dev.arthur.messaging.domain.EventType;
import dev.arthur.sandbox.messaging.CounterPing;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@RestController
@RequestMapping("/api/counters")
@RequiredArgsConstructor
public class CounterController {

    private static final EventType TYPE = new EventType("counter", "pinged");

    private final EventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    @PostMapping
    public CounterPing createCounter() {
        CounterPing ping = new CounterPing(UUID.randomUUID(), 0);
        eventPublisher.publish(TYPE, objectMapper.writeValueAsString(ping));
        return ping;
    }
}
