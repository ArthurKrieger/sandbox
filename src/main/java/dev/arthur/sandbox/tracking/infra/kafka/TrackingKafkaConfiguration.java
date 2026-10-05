package dev.arthur.sandbox.tracking.infra.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/** The write side owns the tracking topic: it is created on startup if it doesn't exist yet. */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(TrackingProperties.class)
class TrackingKafkaConfiguration {

    @Bean
    NewTopic trackingTopic(TrackingProperties properties) {
        return TopicBuilder.name(properties.topic())
                .partitions(properties.partitions())
                .replicas(1)
                .config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(properties.retention().toMillis()))
                .build();
    }
}
