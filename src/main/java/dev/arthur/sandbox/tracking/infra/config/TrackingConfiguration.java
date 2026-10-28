package dev.arthur.sandbox.tracking.infra.config;

import dev.arthur.sandbox.tracking.application.TrackingApplicationService;
import dev.arthur.sandbox.tracking.domain.LocationEventLog;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.time.Clock;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(TrackingProperties.class)
class TrackingConfiguration {

    @Bean
    TrackingApplicationService trackingApplicationService(@NonNull LocationEventLog eventLog, @NonNull Clock clock) {
        return new TrackingApplicationService(eventLog, clock);
    }

    // The write side owns the topic, so it creates it on startup when it doesn't exist yet.
    @Bean
    NewTopic trackingTopic(@NonNull TrackingProperties properties) {
        return TopicBuilder.name(properties.topic())
                .partitions(properties.partitions())
                .replicas(1)
                .config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(properties.retention().toMillis()))
                .build();
    }
}
