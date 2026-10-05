package dev.arthur.sandbox.tracking.infra.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * The tracking topic. {@code retention} is how long events stay in the log, and so how far back the read side can
 * replay; it is also how long a real device's locations are kept.
 */
@ConfigurationProperties("sandbox.tracking")
public record TrackingProperties(String topic, int partitions, Duration retention, Duration sendTimeout) {
}
