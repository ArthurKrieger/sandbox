package dev.arthur.sandbox.user.domain;

import org.jspecify.annotations.NonNull;

public interface UserEventPublisher {

    void created(@NonNull User user);
}
