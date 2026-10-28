package dev.arthur.sandbox.tracking.application;

import dev.arthur.sandbox.tracking.domain.LocationEventLog;
import dev.arthur.sandbox.tracking.domain.LocationRecorded;
import org.jspecify.annotations.NonNull;
import org.springframework.util.Assert;

public record RecordedLocation(@NonNull LocationRecorded event, LocationEventLog.@NonNull Appended appended) {

    public RecordedLocation {
        Assert.notNull(event, "event is required");
        Assert.notNull(appended, "appended is required");
    }
}
