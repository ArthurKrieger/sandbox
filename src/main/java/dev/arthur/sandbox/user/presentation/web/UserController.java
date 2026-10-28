package dev.arthur.sandbox.user.presentation.web;

import dev.arthur.sandbox.user.application.UserApplicationService;
import dev.arthur.sandbox.user.domain.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserApplicationService users;

    public record UserResponse(long id, String username, String email, boolean active) {

        static UserResponse from(@NonNull User user) {
            return new UserResponse(user.id(), user.username(), user.email(), user.active());
        }
    }

    public record CreateUserRequest(@NotNull String username, @NotNull String email) {
    }

    @GetMapping
    public List<UserResponse> listUsers() {
        return users.list().stream().map(UserResponse::from).toList();
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable long id) {
        return UserResponse.from(users.get(id));
    }

    @PostMapping
    public UserResponse createUser(@Valid @RequestBody @NonNull CreateUserRequest request) {
        return UserResponse.from(users.create(request.username(), request.email()));
    }
}
