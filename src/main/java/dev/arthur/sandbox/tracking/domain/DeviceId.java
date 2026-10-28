package dev.arthur.sandbox.tracking.domain;

import org.jspecify.annotations.NonNull;
import org.springframework.util.Assert;

import java.util.UUID;

// The browser picks a random UUID, so a device's track is only findable by whoever knows its id; that is what keeps
// a real device's locations private.
public record DeviceId(@NonNull UUID value) {

    public DeviceId {
        Assert.notNull(value, "device id is required");
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
