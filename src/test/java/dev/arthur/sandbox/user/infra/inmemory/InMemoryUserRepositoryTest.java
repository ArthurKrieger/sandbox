package dev.arthur.sandbox.user.infra.inmemory;

import dev.arthur.sandbox.user.domain.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryUserRepositoryTest {

    private final InMemoryUserRepository users = new InMemoryUserRepository();

    @Test
    void listsTheKnownUsers() {
        assertThat(users.findAll()).extracting(User::username).containsExactly("arthur", "john", "jane");
        assertThatThrownBy(() -> users.findAll().clear()).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void makesUpAUserForAnyId() {
        assertThat(users.findById(42)).isEqualTo(new User(42, "user_42", "user42@example.com", true));
    }
}
