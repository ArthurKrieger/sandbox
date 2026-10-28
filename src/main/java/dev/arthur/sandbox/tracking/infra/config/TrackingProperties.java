package dev.arthur.sandbox.tracking.infra.config;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

import java.time.Duration;

// retention is how long events stay in the log, so how far back the read side can replay, and how long a real
// device's locations are kept.
@ConfigurationProperties("sandbox.tracking")
public record TrackingProperties(@NonNull String topic, int partitions, @NonNull Duration retention,
                                 @NonNull Duration sendTimeout) {

    public TrackingProperties {
        Assert.hasText(topic, "sandbox.tracking.topic is required");
        Assert.isTrue(partitions > 0, "sandbox.tracking.partitions must be positive");
        Assert.notNull(retention, "sandbox.tracking.retention is required");
        Assert.notNull(sendTimeout, "sandbox.tracking.send-timeout is required");
    }
}
