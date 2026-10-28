package dev.arthur.sandbox.tracking.domain;

import org.jspecify.annotations.NonNull;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public record LocationRecorded(@NonNull UUID eventId, @NonNull DeviceId deviceId, @NonNull DeviceKind kind,
                               @NonNull Position position, @NonNull Instant recordedAt, @NonNull Instant receivedAt) {

    static final Duration MAX_CLOCK_SKEW = Duration.ofSeconds(30);
    static final Duration MAX_AGE = Duration.ofMinutes(10);

    public LocationRecorded {
        Assert.notNull(eventId, "event id is required");
        Assert.notNull(deviceId, "device id is required");
        Assert.notNull(kind, "kind is required");
        Assert.notNull(position, "position is required");
        Assert.notNull(recordedAt, "recordedAt is required");
        Assert.notNull(receivedAt, "receivedAt is required");
    }

    public static LocationRecorded of(@NonNull RecordLocation command, @NonNull Instant now) {
        if (command.recordedAt().isAfter(now.plus(MAX_CLOCK_SKEW))) {
            throw new InvalidLocationException("recordedAt is in the future");
        }
        if (command.recordedAt().isBefore(now.minus(MAX_AGE))) {
            throw new InvalidLocationException("recordedAt is more than " + MAX_AGE.toMinutes() + " minutes old");
        }
        return new LocationRecorded(UUID.randomUUID(), command.deviceId(), command.kind(), command.position(),
                command.recordedAt(), now);
    }
}
