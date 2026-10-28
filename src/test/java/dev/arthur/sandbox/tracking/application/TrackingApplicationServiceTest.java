package dev.arthur.sandbox.tracking.application;

import dev.arthur.sandbox.tracking.domain.DeviceId;
import dev.arthur.sandbox.tracking.domain.DeviceKind;
import dev.arthur.sandbox.tracking.domain.EventLogUnavailableException;
import dev.arthur.sandbox.tracking.domain.InvalidLocationException;
import dev.arthur.sandbox.tracking.domain.LocationEventLog;
import dev.arthur.sandbox.tracking.domain.LocationRecorded;
import dev.arthur.sandbox.tracking.domain.Position;
import dev.arthur.sandbox.tracking.domain.RecordLocation;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TrackingApplicationServiceTest {

    private static final Instant NOW = Instant.parse("2026-10-05T12:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
    private static final DeviceId DEVICE = new DeviceId(UUID.randomUUID());

    private final List<LocationRecorded> appended = new ArrayList<>();

    @Test
    void appendsTheLocationAndSaysWhereItLanded() {
        final TrackingApplicationService tracking = new TrackingApplicationService(event -> {
            appended.add(event);
            return new LocationEventLog.Appended(2, 41);
        }, CLOCK);

        final RecordedLocation recorded = tracking.applyCommand(new RecordLocation(DEVICE, DeviceKind.REAL,
                new Position(1, 2, null), NOW.minusSeconds(1)));

        assertThat(appended).containsExactly(recorded.event());
        assertThat(recorded.event().receivedAt()).isEqualTo(NOW);
        assertThat(recorded.appended()).isEqualTo(new LocationEventLog.Appended(2, 41));
    }

    @Test
    void appendsNothingForAnInvalidLocation() {
        final TrackingApplicationService tracking = new TrackingApplicationService(event -> {
            appended.add(event);
            return new LocationEventLog.Appended(0, 0);
        }, CLOCK);

        assertThatThrownBy(() -> tracking.applyCommand(new RecordLocation(DEVICE, DeviceKind.REAL,
                new Position(1, 2, null), NOW.plusSeconds(3600))))
                .isInstanceOf(InvalidLocationException.class);
        assertThat(appended).isEmpty();
    }

    @Test
    void passesOnAnUnavailableLog() {
        final TrackingApplicationService tracking = new TrackingApplicationService(event -> {
            throw new EventLogUnavailableException("timed out", new TimeoutException());
        }, CLOCK);

        assertThatThrownBy(() -> tracking.applyCommand(new RecordLocation(DEVICE, DeviceKind.REAL,
                new Position(1, 2, null), NOW)))
                .isInstanceOf(EventLogUnavailableException.class);
    }
}
