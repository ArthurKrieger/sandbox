package dev.arthur.sandbox.tracking.domain;

import org.jspecify.annotations.NonNull;
import org.springframework.util.Assert;

import java.time.Instant;

public record RecordLocation(@NonNull DeviceId deviceId, @NonNull DeviceKind kind, @NonNull Position position,
                             @NonNull Instant recordedAt) {

    public RecordLocation {
        Assert.notNull(deviceId, "device id is required");
        Assert.notNull(kind, "kind is required");
        Assert.notNull(position, "position is required");
        Assert.notNull(recordedAt, "recordedAt is required");
    }
}
