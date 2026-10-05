package dev.arthur.sandbox.presentation;

import dev.arthur.sandbox.tracking.application.TrackingService;
import dev.arthur.sandbox.tracking.domain.DeviceId;
import dev.arthur.sandbox.tracking.domain.DeviceKind;
import dev.arthur.sandbox.tracking.domain.EventLogUnavailableException;
import dev.arthur.sandbox.tracking.domain.InvalidLocationException;
import dev.arthur.sandbox.tracking.domain.Position;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

/**
 * Tracking commands: devices report locations here and they are appended to the Kafka event log. There are no
 * tracking queries on this service; the consumer service projects the log and serves them.
 */
@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final TrackingService trackingService;

    public record LocationReport(@NotNull DeviceKind kind, @NotNull Double latitude, @NotNull Double longitude,
                                 @PositiveOrZero Double accuracyMeters, @NotNull Instant recordedAt) {
    }

    /** Where the event landed in the log: the read side shows it next to each point. */
    public record RecordedLocation(UUID eventId, int partition, long offset) {
    }

    @PostMapping("/devices/{deviceId}/locations")
    public ResponseEntity<RecordedLocation> report(@PathVariable UUID deviceId,
                                                   @Valid @RequestBody LocationReport report) {
        try {
            TrackingService.Recorded recorded = trackingService.record(new DeviceId(deviceId), report.kind(),
                    new Position(report.latitude(), report.longitude(), report.accuracyMeters()),
                    report.recordedAt());
            return ResponseEntity.accepted().body(new RecordedLocation(recorded.event().eventId(),
                    recorded.appended().partition(), recorded.appended().offset()));
        } catch (InvalidLocationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (EventLogUnavailableException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "the event log is unavailable", e);
        }
    }
}
