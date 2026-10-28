package dev.arthur.sandbox.user.presentation.web;

import dev.arthur.sandbox.user.application.UserApplicationService;
import dev.arthur.sandbox.user.domain.User;
import dev.arthur.sandbox.user.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {

    private static final User ARTHUR = new User(1L, "arthur", "arthur@example.com", true);
    private static final Instant NOW = Instant.parse("2026-10-25T12:00:00Z");

    private final List<User> created = new ArrayList<>();
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        final UserRepository users = new UserRepository() {
            @Override
            public List<User> findAll() {
                return List.of(ARTHUR);
            }

            @Override
            public User findById(long id) {
                return new User(id, "user_" + id, "user" + id + "@example.com", false);
            }
        };
        mvc = MockMvcBuilders.standaloneSetup(new UserController(
                new UserApplicationService(users, created::add, Clock.fixed(NOW, ZoneOffset.UTC)))).build();
    }

    @Test
    void listsUsers() throws Exception {
        mvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("arthur"))
                .andExpect(jsonPath("$[0].email").value("arthur@example.com"))
                .andExpect(jsonPath("$[0].active").value(true));
    }

    @Test
    void returnsAUserById() throws Exception {
        mvc.perform(get("/api/users/{id}", 7))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.username").value("user_7"))
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void createsAUserAndPublishesIt() throws Exception {
        mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"ana\", \"email\": \"ana@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(NOW.toEpochMilli()))
                .andExpect(jsonPath("$.username").value("ana"))
                .andExpect(jsonPath("$.active").value(true));

        assertThat(created).containsExactly(new User(NOW.toEpochMilli(), "ana", "ana@example.com", true));
    }

    @Test
    void rejectsAUserWithoutAnEmail() throws Exception {
        mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content("{\"username\": \"ana\"}"))
                .andExpect(status().isBadRequest());

        assertThat(created).isEmpty();
    }
}
