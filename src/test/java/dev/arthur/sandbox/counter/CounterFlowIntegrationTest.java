package dev.arthur.sandbox.counter;

import dev.arthur.messaging.infra.outbox.OutboxEvent;
import dev.arthur.messaging.infra.outbox.OutboxEventRepository;
import dev.arthur.sandbox.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class CounterFlowIntegrationTest {

    @Autowired
    OutboxEventRepository outbox;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void storesAnnouncesAndServesANewCounter() throws Exception {
        final String body = mvc.perform(post("/api/counters"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        final UUID counter = UUID.fromString(objectMapper.readTree(body).get("id").asString());

        assertThat(published(counter)).singleElement().satisfies(event -> {
            assertThat(event.type()).isEqualTo("counter.created");
            assertThat(objectMapper.readTree(event.payload()).propertyNames()).containsExactly("counterId");
        });
        mvc.perform(get("/api/counters/{id}", counter))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(counter.toString()))
                .andExpect(jsonPath("$.createdBy").isNotEmpty())
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    void answersNotFoundForACounterItNeverCreated() throws Exception {
        mvc.perform(get("/api/counters/{id}", UUID.randomUUID())).andExpect(status().isNotFound());
    }

    private List<OutboxEvent> published(UUID counter) {
        return StreamSupport.stream(outbox.findAll().spliterator(), false)
                .filter(event -> event.payload().contains(counter.toString()))
                .toList();
    }
}
