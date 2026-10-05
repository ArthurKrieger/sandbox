package dev.arthur.sandbox.tracking.application;

import dev.arthur.sandbox.tracking.domain.DeviceId;
import dev.arthur.sandbox.tracking.domain.DeviceKind;
import dev.arthur.sandbox.tracking.domain.LocationEventLog;
import dev.arthur.sandbox.tracking.domain.LocationRecorded;
import dev.arthur.sandbox.tracking.domain.Position;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

/**
 * The tracking write side: validates a reported location and appends it to the event log. Reads are served by
 * the consumer service's projection of that log, never from here.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TrackingService {

    private final LocationEventLog eventLog;
    private final Clock clock;

    public Recorded record(DeviceId deviceId, DeviceKind kind, Position position, Instant recordedAt) {
        LocationRecorded event = LocationRecorded.of(deviceId, kind, position, recordedAt, clock.instant());
        LocationEventLog.Appended appended = eventLog.append(event);
        log.debug("location {} for {} device {} stored at partition {} offset {}", event.eventId(), kind, deviceId,
                appended.partition(), appended.offset());
        return new Recorded(event, appended);
    }

    public record Recorded(LocationRecorded event, LocationEventLog.Appended appended) {
    }
}
