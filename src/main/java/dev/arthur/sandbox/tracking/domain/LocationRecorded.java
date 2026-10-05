package dev.arthur.sandbox.tracking.domain;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * The only tracking event: a device reported where it was at {@code recordedAt}. Events are facts appended to
 * the Kafka log, which is the tracking event store; nothing about a device is stored on the write side.
 */
public record LocationRecorded(UUID eventId, DeviceId deviceId, DeviceKind kind, Position position, Instant recordedAt,
                               Instant receivedAt) {

    /** How far ahead of the server's clock a device's clock may run. */
    static final Duration MAX_CLOCK_SKEW = Duration.ofSeconds(30);
    /** How late a location may arrive: older ones would be recorded long after the fact. */
    static final Duration MAX_AGE = Duration.ofMinutes(10);

    public LocationRecorded {
        Objects.requireNonNull(eventId, "eventId");
        Objects.requireNonNull(deviceId, "deviceId");
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(position, "position");
        Objects.requireNonNull(recordedAt, "recordedAt");
        Objects.requireNonNull(receivedAt, "receivedAt");
    }

    /** Records a location the device reported, received by the server at {@code now}. */
    public static LocationRecorded of(DeviceId deviceId, DeviceKind kind, Position position, Instant recordedAt,
                                      Instant now) {
        if (recordedAt.isAfter(now.plus(MAX_CLOCK_SKEW))) {
            throw new InvalidLocationException("recordedAt is in the future");
        }
        if (recordedAt.isBefore(now.minus(MAX_AGE))) {
            throw new InvalidLocationException("recordedAt is more than " + MAX_AGE.toMinutes() + " minutes old");
        }
        return new LocationRecorded(UUID.randomUUID(), deviceId, kind, position, recordedAt, now);
    }
}
