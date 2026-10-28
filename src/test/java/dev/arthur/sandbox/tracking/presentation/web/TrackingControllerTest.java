package dev.arthur.sandbox.tracking.presentation.web;

import dev.arthur.sandbox.shared.presentation.web.GlobalExceptionHandler;
import dev.arthur.sandbox.tracking.application.RecordedLocation;
import dev.arthur.sandbox.tracking.application.TrackingApplicationService;
import dev.arthur.sandbox.tracking.domain.DeviceId;
import dev.arthur.sandbox.tracking.domain.DeviceKind;
import dev.arthur.sandbox.tracking.domain.EventLogUnavailableException;
import dev.arthur.sandbox.tracking.domain.InvalidLocationException;
import dev.arthur.sandbox.tracking.domain.LocationEventLog;
import dev.arthur.sandbox.tracking.domain.LocationRecorded;
import dev.arthur.sandbox.tracking.domain.Position;
import dev.arthur.sandbox.tracking.domain.RecordLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TrackingControllerTest {

    private static final UUID DEVICE = UUID.fromString("0a1b2c3d-1111-4222-8333-444455556666");
    private static final Instant RECORDED_AT = Instant.parse("2026-10-05T12:00:00Z");

    private final TrackingApplicationService tracking = mock(TrackingApplicationService.class);
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new TrackingController(tracking))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private static String report(String overrides) {
        return """
                {"kind": "SIMULATED", "latitude": 38.7223, "longitude": -9.1393, "accuracyMeters": 5,
                 "recordedAt": "2026-10-05T12:00:00Z"%s}""".formatted(overrides);
    }

    @Test
    void acceptsALocationAndSaysWhereInTheLogItLanded() throws Exception {
        final LocationRecorded event = new LocationRecorded(UUID.randomUUID(), new DeviceId(DEVICE),
                DeviceKind.SIMULATED, new Position(38.7223, -9.1393, 5.0), RECORDED_AT, RECORDED_AT);
        when(tracking.applyCommand(any())).thenReturn(new RecordedLocation(event, new LocationEventLog.Appended(2, 41)));

        mvc.perform(post("/api/tracking/devices/{id}/locations", DEVICE)
                        .contentType(MediaType.APPLICATION_JSON).content(report("")))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.eventId").value(event.eventId().toString()))
                .andExpect(jsonPath("$.partition").value(2))
                .andExpect(jsonPath("$.offset").value(41));

        verify(tracking).applyCommand(new RecordLocation(new DeviceId(DEVICE), DeviceKind.SIMULATED,
                new Position(38.7223, -9.1393, 5.0), RECORDED_AT));
    }

    @Test
    void rejectsAReportMissingItsCoordinates() throws Exception {
        mvc.perform(post("/api/tracking/devices/{id}/locations", DEVICE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"kind\": \"REAL\", \"recordedAt\": \"2026-10-05T12:00:00Z\"}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(tracking);
    }

    @Test
    void rejectsAnUnknownDeviceKind() throws Exception {
        mvc.perform(post("/api/tracking/devices/{id}/locations", DEVICE)
                        .contentType(MediaType.APPLICATION_JSON).content(report(", \"kind\": \"DRONE\"")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsADeviceIdThatIsNotAUuid() throws Exception {
        mvc.perform(post("/api/tracking/devices/{id}/locations", "not-a-uuid")
                        .contentType(MediaType.APPLICATION_JSON).content(report("")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsCoordinatesOffTheGlobe() throws Exception {
        mvc.perform(post("/api/tracking/devices/{id}/locations", DEVICE)
                        .contentType(MediaType.APPLICATION_JSON).content(report(", \"latitude\": 91")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("latitude must be between -90 and 90"));

        verifyNoInteractions(tracking);
    }

    @Test
    void turnsAnInvalidLocationIntoABadRequest() throws Exception {
        when(tracking.applyCommand(any())).thenThrow(new InvalidLocationException("recordedAt is in the future"));

        mvc.perform(post("/api/tracking/devices/{id}/locations", DEVICE)
                        .contentType(MediaType.APPLICATION_JSON).content(report("")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("recordedAt is in the future"));
    }

    @Test
    void answersServiceUnavailableWhenTheLogCanNotConfirm() throws Exception {
        when(tracking.applyCommand(any()))
                .thenThrow(new EventLogUnavailableException("timed out", new TimeoutException()));

        mvc.perform(post("/api/tracking/devices/{id}/locations", DEVICE)
                        .contentType(MediaType.APPLICATION_JSON).content(report("")))
                .andExpect(status().isServiceUnavailable());
    }
}
