package dev.arthur.sandbox.user.domain;

import org.jspecify.annotations.NonNull;
import org.springframework.util.Assert;

public record User(long id, @NonNull String username, @NonNull String email, boolean active) {

    public User {
        Assert.notNull(username, "username is required");
        Assert.notNull(email, "email is required");
    }
}
