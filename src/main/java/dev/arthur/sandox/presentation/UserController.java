package dev.arthur.sandox.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

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
}
