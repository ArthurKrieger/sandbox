package dev.arthur.sandbox.tracking.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LocationRecordedTest {

    private static final Instant NOW = Instant.parse("2026-10-05T12:00:00Z");
    private static final DeviceId DEVICE = new DeviceId(UUID.randomUUID());
    private static final Position LISBON = new Position(38.7223, -9.1393, 12.0);

    private static RecordLocation report(Instant recordedAt) {
        return new RecordLocation(DEVICE, DeviceKind.SIMULATED, LISBON, recordedAt);
    }

    @Test
    void recordsALocationWithANewEventIdAndTheTimeItArrived() {
        final LocationRecorded event = LocationRecorded.of(report(NOW.minusSeconds(2)), NOW);

        assertThat(event.eventId()).isNotNull();
        assertThat(event.deviceId()).isEqualTo(DEVICE);
        assertThat(event.kind()).isEqualTo(DeviceKind.SIMULATED);
        assertThat(event.position()).isEqualTo(LISBON);
        assertThat(event.recordedAt()).isEqualTo(NOW.minusSeconds(2));
        assertThat(event.receivedAt()).isEqualTo(NOW);
        assertThat(LocationRecorded.of(report(NOW), NOW).eventId()).isNotEqualTo(event.eventId());
    }

    @Test
    void allowsADeviceClockSlightlyAhead() {
        final Instant ahead = NOW.plus(LocationRecorded.MAX_CLOCK_SKEW);

        assertThat(LocationRecorded.of(report(ahead), NOW).recordedAt()).isEqualTo(ahead);
    }

    @Test
    void rejectsALocationFromTheFuture() {
        final Instant future = NOW.plus(LocationRecorded.MAX_CLOCK_SKEW).plusSeconds(1);

        assertThatThrownBy(() -> LocationRecorded.of(report(future), NOW))
                .isInstanceOf(InvalidLocationException.class)
                .hasMessageContaining("future");
    }

    @Test
    void rejectsALocationReportedTooLate() {
        final Instant stale = NOW.minus(LocationRecorded.MAX_AGE).minusSeconds(1);

        assertThatThrownBy(() -> LocationRecorded.of(report(stale), NOW))
                .isInstanceOf(InvalidLocationException.class)
                .hasMessageContaining("old");
    }

    @ParameterizedTest
    @CsvSource({"90.0001, 0", "-90.0001, 0", "0, 180.0001", "0, -180.0001", "NaN, 0", "0, NaN"})
    void rejectsCoordinatesOffTheGlobe(double latitude, double longitude) {
        assertThatThrownBy(() -> new Position(latitude, longitude, null)).isInstanceOf(InvalidLocationException.class);
    }

    @Test
    void acceptsTheEdgesOfTheGlobeAndAMissingAccuracy() {
        assertThat(new Position(90, 180, null).accuracyMeters()).isNull();
        assertThat(new Position(-90, -180, 0.0).accuracyMeters()).isZero();
    }

    @Test
    void rejectsANegativeAccuracy() {
        assertThatThrownBy(() -> new Position(0, 0, -1.0)).isInstanceOf(InvalidLocationException.class);
    }

    @Test
    void requiresEveryPartOfAReport() {
        assertThatThrownBy(() -> new RecordLocation(null, DeviceKind.REAL, LISBON, NOW))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new RecordLocation(DEVICE, DeviceKind.REAL, LISBON, null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
