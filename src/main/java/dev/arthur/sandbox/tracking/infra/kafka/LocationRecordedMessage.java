package dev.arthur.sandbox.tracking.infra.kafka;

import dev.arthur.sandbox.tracking.domain.LocationRecorded;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.UUID;

// The consumer service keeps its own copy of this shape, so change both together.
record LocationRecordedMessage(String type, UUID eventId, UUID deviceId, String kind, double latitude,
                               double longitude, @Nullable Double accuracyMeters, Instant recordedAt,
                               Instant receivedAt) {

    static final String TYPE = "location.recorded";

    static LocationRecordedMessage from(@NonNull LocationRecorded event) {
        return new LocationRecordedMessage(TYPE, event.eventId(), event.deviceId().value(), event.kind().name(),
                event.position().latitude(), event.position().longitude(), event.position().accuracyMeters(),
                event.recordedAt(), event.receivedAt());
    }
}
