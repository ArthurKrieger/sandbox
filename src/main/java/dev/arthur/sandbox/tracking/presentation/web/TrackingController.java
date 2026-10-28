package dev.arthur.sandbox.tracking.presentation.web;

import dev.arthur.sandbox.tracking.application.RecordedLocation;
import dev.arthur.sandbox.tracking.application.TrackingApplicationService;
import dev.arthur.sandbox.tracking.domain.DeviceId;
import dev.arthur.sandbox.tracking.domain.DeviceKind;
import dev.arthur.sandbox.tracking.domain.Position;
import dev.arthur.sandbox.tracking.domain.RecordLocation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

// Only commands: the consumer service projects the log and serves the tracking queries.
@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final TrackingApplicationService tracking;

    public record LocationReport(@NotNull DeviceKind kind, @NotNull Double latitude, @NotNull Double longitude,
                                 @Nullable @PositiveOrZero Double accuracyMeters, @NotNull Instant recordedAt) {
    }

    public record LocationResponse(UUID eventId, int partition, long offset) {

        static LocationResponse from(@NonNull RecordedLocation recorded) {
            return new LocationResponse(recorded.event().eventId(), recorded.appended().partition(),
                    recorded.appended().offset());
        }
    }

    @PostMapping("/devices/{deviceId}/locations")
    public ResponseEntity<LocationResponse> report(@PathVariable @NonNull UUID deviceId,
                                                   @Valid @RequestBody @NonNull LocationReport report) {
        final RecordedLocation recorded = tracking.applyCommand(new RecordLocation(new DeviceId(deviceId),
                report.kind(), new Position(report.latitude(), report.longitude(), report.accuracyMeters()),
                report.recordedAt()));
        return ResponseEntity.accepted().body(LocationResponse.from(recorded));
    }
}
