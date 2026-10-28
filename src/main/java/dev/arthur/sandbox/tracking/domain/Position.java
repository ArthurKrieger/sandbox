package dev.arthur.sandbox.tracking.domain;

import org.jspecify.annotations.Nullable;

// The negated comparisons also reject NaN.
public record Position(double latitude, double longitude, @Nullable Double accuracyMeters) {

    public Position {
        if (!(latitude >= -90 && latitude <= 90)) {
            throw new InvalidLocationException("latitude must be between -90 and 90");
        }
        if (!(longitude >= -180 && longitude <= 180)) {
            throw new InvalidLocationException("longitude must be between -180 and 180");
        }
        if (accuracyMeters != null && !(accuracyMeters >= 0)) {
            throw new InvalidLocationException("accuracy must not be negative");
        }
    }
}
