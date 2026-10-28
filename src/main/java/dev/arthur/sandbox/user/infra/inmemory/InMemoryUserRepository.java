package dev.arthur.sandbox.user.infra.inmemory;

import dev.arthur.sandbox.user.domain.User;
import dev.arthur.sandbox.user.domain.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class InMemoryUserRepository implements UserRepository {

    private static final List<User> USERS = List.of(
            new User(1L, "arthur", "arthur@example.com", true),
            new User(2L, "john", "john@example.com", true),
            new User(3L, "jane", "jane@example.com", false));

    @Override
    public List<User> findAll() {
        return USERS;
    }

    @Override
    public User findById(long id) {
        return new User(id, "user_" + id, "user" + id + "@example.com", true);
    }
}
