package dev.arthur.sandbox.shared.presentation.web;

import dev.arthur.sandbox.shared.domain.NotFoundException;
import dev.arthur.sandbox.shared.domain.UnavailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mvc;

    @RestController
    static class FailingController {

        @GetMapping("/not-found")
        void notFound() {
            throw new NotFoundException("thing not found") {
            };
        }

        @GetMapping("/unavailable")
        void unavailable() {
            throw new UnavailableException("broker is down", new IllegalStateException("cause")) {
            };
        }

        @GetMapping("/unexpected")
        void unexpected() {
            throw new IllegalStateException("database password is hunter2");
        }
    }

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new FailingController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void mapsNotFound() throws Exception {
        mvc.perform(get("/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("thing not found"));
    }

    @Test
    void mapsUnavailable() throws Exception {
        mvc.perform(get("/unavailable"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.detail").value("broker is down"));
    }

    @Test
    void hidesTheDetailsOfAnUnexpectedFailure() throws Exception {
        mvc.perform(get("/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.detail").value("unexpected error"));
    }

    @Test
    void stillMapsSpringsOwnErrors() throws Exception {
        mvc.perform(get("/nowhere")).andExpect(status().isNotFound());
    }
}
