package dev.arthur.sandbox.presentation;

import dev.arthur.messaging.domain.EventPublisher;
import dev.arthur.messaging.domain.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private static final EventType USER_CREATED = new EventType("user", "created");

    private final EventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    public record User(long id, String username, String email, boolean active) {
    }

    public record CreateUserRequest(String username, String email) {
    }

    @GetMapping
    public List<User> listUsers() {
        return List.of(
                new User(1L, "arthur", "arthur@example.com", true),
                new User(2L, "john", "john@example.com", true),
                new User(3L, "jane", "jane@example.com", false));
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable long id) {
        return new User(id, "user_" + id, "user" + id + "@example.com", true);
    }

    @PostMapping
    public User createUser(@RequestBody CreateUserRequest request) {
        User user = new User(System.currentTimeMillis(), request.username(), request.email(), true);
        eventPublisher.publish(USER_CREATED, objectMapper.writeValueAsString(user));
        return user;
    }
}
