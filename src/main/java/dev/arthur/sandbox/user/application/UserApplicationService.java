package dev.arthur.sandbox.user.application;

import dev.arthur.sandbox.user.domain.User;
import dev.arthur.sandbox.user.domain.UserEventPublisher;
import dev.arthur.sandbox.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;

@RequiredArgsConstructor
public class UserApplicationService {

    private final UserRepository users;
    private final UserEventPublisher eventPublisher;
    private final Clock clock;

    public List<User> list() {
        return users.findAll();
    }

    public User get(long id) {
        return users.findById(id);
    }

    @Transactional
    public User create(@NonNull String username, @NonNull String email) {
        final User user = new User(clock.millis(), username, email, true);
        eventPublisher.created(user);
        return user;
    }
}
