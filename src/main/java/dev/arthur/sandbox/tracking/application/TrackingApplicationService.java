package dev.arthur.sandbox.tracking.application;

import dev.arthur.sandbox.tracking.domain.LocationEventLog;
import dev.arthur.sandbox.tracking.domain.LocationRecorded;
import dev.arthur.sandbox.tracking.domain.RecordLocation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

import java.time.Clock;

@Slf4j
@RequiredArgsConstructor
public class TrackingApplicationService {

    private final LocationEventLog eventLog;
    private final Clock clock;

    public RecordedLocation applyCommand(@NonNull RecordLocation command) {
        final LocationRecorded event = LocationRecorded.of(command, clock.instant());
        final LocationEventLog.Appended appended = eventLog.append(event);
        log.debug("location {} for {} device {} stored at partition {} offset {}", event.eventId(), event.kind(),
                event.deviceId(), appended.partition(), appended.offset());
        return new RecordedLocation(event, appended);
    }
}
