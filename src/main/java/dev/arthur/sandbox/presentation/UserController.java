package dev.arthur.sandbox.presentation;

import dev.arthur.messaging.domain.EventPublisher;
import dev.arthur.messaging.domain.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final EventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    @GetMapping
    public List<Map<String, Object>> listUsers() {
        return List.of(
            Map.of(
                "id", 1L,
                "username", "arthur",
                "email", "arthur@example.com",
                "active", true
            ),
            Map.of(
                "id", 2L,
                "username", "john",
                "email", "john@example.com",
                "active", true
            ),
            Map.of(
                "id", 3L,
                "username", "jane",
                "email", "jane@example.com",
                "active", false
            )
        );
    }

    @GetMapping("/{id}")
    public Map<String, Object> getUserById(@PathVariable Long id) {
        return Map.of(
            "id", id,
            "username", "user_" + id,
            "email", "user" + id + "@example.com",
            "active", true
        );
    }

    // Fake persistence, same as the rest of this controller -- the point
    // here is testing the SNS publish path, not real user creation.
    @PostMapping
    public Map<String, Object> createUser(@RequestBody Map<String, Object> request) {
        Map<String, Object> user = Map.of(
            "id", System.currentTimeMillis(),
            "username", request.getOrDefault("username", "unknown"),
            "email", request.getOrDefault("email", ""),
            "active", true
        );

        try {
            eventPublisher.publish(new EventType("user", "created"), objectMapper.writeValueAsString(user));
        } catch (JacksonException e) {
            log.error("failed to serialize user.created event", e);
        }

        return user;
    }
}
