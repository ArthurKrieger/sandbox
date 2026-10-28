package dev.arthur.sandbox.counter.presentation.web;

import dev.arthur.sandbox.counter.application.CounterApplicationService;
import dev.arthur.sandbox.counter.domain.Counter;
import dev.arthur.sandbox.counter.domain.CounterId;
import dev.arthur.sandbox.counter.domain.CounterNotFoundException;
import dev.arthur.sandbox.shared.presentation.web.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CounterControllerTest {

    private static final CounterId ID = CounterId.of(UUID.fromString("0a1b2c3d-1111-4222-8333-444455556666"));
    private static final Counter COUNTER = new Counter(ID, "pod-p", Instant.parse("2026-10-25T12:00:00Z"));

    private final CounterApplicationService counters = mock(CounterApplicationService.class);
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new CounterController(counters))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createsACounter() throws Exception {
        when(counters.create()).thenReturn(COUNTER);

        mvc.perform(post("/api/counters"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/counters/" + ID))
                .andExpect(jsonPath("$.id").value(ID.toString()));
    }

    @Test
    void saysWhoCreatedACounterAndWhen() throws Exception {
        when(counters.get(ID)).thenReturn(COUNTER);

        mvc.perform(get("/api/counters/{id}", ID.value()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID.toString()))
                .andExpect(jsonPath("$.createdBy").value("pod-p"))
                .andExpect(jsonPath("$.createdAt").value("2026-10-25T12:00:00Z"));
    }

    @Test
    void answersNotFoundForAnUnknownCounter() throws Exception {
        when(counters.get(ID)).thenThrow(new CounterNotFoundException(ID));

        mvc.perform(get("/api/counters/{id}", ID.value())).andExpect(status().isNotFound());
    }

    @Test
    void rejectsAnIdThatIsNotAUuid() throws Exception {
        mvc.perform(get("/api/counters/{id}", "nope")).andExpect(status().isBadRequest());
    }
}
