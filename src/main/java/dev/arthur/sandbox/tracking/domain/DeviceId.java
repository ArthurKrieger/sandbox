package dev.arthur.sandbox.tracking.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * A tracked device. The browser picks a random UUID, so a device's track is only findable by whoever knows its
 * id; that is what keeps a real device's locations private.
 */
public record DeviceId(UUID value) {

    public DeviceId {
        Objects.requireNonNull(value, "value");
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
