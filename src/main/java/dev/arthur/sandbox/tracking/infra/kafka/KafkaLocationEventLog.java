package dev.arthur.sandbox.tracking.infra.kafka;

import dev.arthur.sandbox.tracking.domain.EventLogUnavailableException;
import dev.arthur.sandbox.tracking.domain.LocationEventLog;
import dev.arthur.sandbox.tracking.domain.LocationRecorded;
import dev.arthur.sandbox.tracking.infra.config.TrackingProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.jspecify.annotations.NonNull;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

// Keyed by device, so each device's events land on one partition, in order.
@Component
@RequiredArgsConstructor
class KafkaLocationEventLog implements LocationEventLog {

    private final KafkaTemplate<String, String> kafka;
    private final ObjectMapper objectMapper;
    private final TrackingProperties properties;

    @Override
    public Appended append(@NonNull LocationRecorded event) {
        final String value = objectMapper.writeValueAsString(LocationRecordedMessage.from(event));
        try {
            final RecordMetadata metadata = kafka.send(properties.topic(), event.deviceId().toString(), value)
                    .get(properties.sendTimeout().toMillis(), TimeUnit.MILLISECONDS)
                    .getRecordMetadata();
            return new Appended(metadata.partition(), metadata.offset());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new EventLogUnavailableException("interrupted while appending " + event.eventId(), e);
        } catch (ExecutionException | TimeoutException e) {
            throw new EventLogUnavailableException("couldn't append " + event.eventId(), e);
        }
    }
}
