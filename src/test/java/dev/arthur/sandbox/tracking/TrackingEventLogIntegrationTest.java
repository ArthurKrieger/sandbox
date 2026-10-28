package dev.arthur.sandbox.tracking;

import dev.arthur.sandbox.TestcontainersConfiguration;
import dev.arthur.sandbox.tracking.application.RecordedLocation;
import dev.arthur.sandbox.tracking.application.TrackingApplicationService;
import dev.arthur.sandbox.tracking.domain.DeviceId;
import dev.arthur.sandbox.tracking.domain.DeviceKind;
import dev.arthur.sandbox.tracking.domain.Position;
import dev.arthur.sandbox.tracking.domain.RecordLocation;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaAdmin;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class TrackingEventLogIntegrationTest {

    private static final String TOPIC = "tracking.locations";

    @Autowired
    TrackingApplicationService tracking;
    @Autowired
    KafkaAdmin kafkaAdmin;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void appendsTheEventKeyedByDeviceWithEverythingTheReadSideNeeds() {
        final DeviceId device = new DeviceId(UUID.randomUUID());
        final Instant recordedAt = Instant.now().minusSeconds(1);

        final RecordedLocation recorded = tracking.applyCommand(new RecordLocation(device, DeviceKind.REAL,
                new Position(38.7223, -9.1393, 8.5), recordedAt));

        final ConsumerRecord<String, String> record = read(recorded.appended().partition(),
                recorded.appended().offset());
        assertThat(record.key()).isEqualTo(device.toString());
        final JsonNode value = objectMapper.readTree(record.value());
        assertThat(value.get("type").asString()).isEqualTo("location.recorded");
        assertThat(value.get("eventId").asString()).isEqualTo(recorded.event().eventId().toString());
        assertThat(value.get("deviceId").asString()).isEqualTo(device.toString());
        assertThat(value.get("kind").asString()).isEqualTo("REAL");
        assertThat(value.get("latitude").asDouble()).isEqualTo(38.7223);
        assertThat(value.get("longitude").asDouble()).isEqualTo(-9.1393);
        assertThat(value.get("accuracyMeters").asDouble()).isEqualTo(8.5);
        assertThat(Instant.parse(value.get("recordedAt").asString())).isEqualTo(recordedAt);
        assertThat(value.has("receivedAt")).isTrue();
    }

    @Test
    void keepsEachDevicesEventsInOrderOnOnePartition() {
        final DeviceId device = new DeviceId(UUID.randomUUID());
        final Position position = new Position(0, 0, null);

        final List<RecordedLocation> recorded = List.of(
                tracking.applyCommand(new RecordLocation(device, DeviceKind.SIMULATED, position, Instant.now())),
                tracking.applyCommand(new RecordLocation(device, DeviceKind.SIMULATED, position, Instant.now())),
                tracking.applyCommand(new RecordLocation(device, DeviceKind.SIMULATED, position, Instant.now())));

        assertThat(recorded).extracting(r -> r.appended().partition())
                .containsOnly(recorded.getFirst().appended().partition());
        assertThat(recorded).extracting(r -> r.appended().offset()).isSorted().doesNotHaveDuplicates();
    }

    @Test
    void createsTheTopicWithThreePartitionsAndTheRetentionWindow() throws Exception {
        try (AdminClient admin = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            assertThat(admin.describeTopics(List.of(TOPIC)).allTopicNames().get().get(TOPIC).partitions()).hasSize(3);
            final ConfigResource topic = new ConfigResource(ConfigResource.Type.TOPIC, TOPIC);
            final Config config = admin.describeConfigs(List.of(topic)).all().get().get(topic);
            assertThat(config.get(TopicConfig.RETENTION_MS_CONFIG).value())
                    .isEqualTo(String.valueOf(Duration.ofHours(24).toMillis()));
        }
    }

    private ConsumerRecord<String, String> read(int partition, long offset) {
        final Map<String, Object> config = Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaAdmin.getConfigurationProperties()
                        .get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG),
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(config)) {
            final TopicPartition topicPartition = new TopicPartition(TOPIC, partition);
            consumer.assign(List.of(topicPartition));
            consumer.seek(topicPartition, offset);
            for (int attempt = 0; attempt < 20; attempt++) {
                for (final ConsumerRecord<String, String> record : consumer.poll(Duration.ofMillis(500))) {
                    if (record.offset() == offset) {
                        return record;
                    }
                }
            }
        }
        throw new AssertionError("no record at partition " + partition + " offset " + offset);
    }
}
