package dev.arthur.sandbox.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

/**
 * Publishes events to the SNS topic sandbox-infra provisions
 * (modules/messaging) -- topic ARN/region come from SNS_TOPIC_ARN/AWS_REGION,
 * read as Spring properties (env var in prod, via Terraform; application.yml
 * default for local dev).
 *
 * Credentials still come from the AWS SDK's own default chain (the ECS task
 * role in prod -- see sandbox-infra's modules/iam sns_topic_arn_publish
 * grant, no access keys anywhere -- or your local `aws configure`/SSO
 * profile) -- only the region is set explicitly here, since the SDK's
 * default region chain reads the OS's own AWS_REGION env var directly, which
 * a Spring property of the same name does NOT populate.
 */
@Slf4j
@Component
public class EventPublisher {

    private final SnsClient snsClient;
    private final String topicArn;

    public EventPublisher(
            @Value("${SNS_TOPIC_ARN:}") String topicArn,
            @Value("${AWS_REGION:sa-east-1}") String region) {
        this.topicArn = topicArn;
        this.snsClient = SnsClient.builder()
                .region(Region.of(region))
                .build();
    }

    public void publish(String eventType, String messageJson) {
        if (topicArn == null || topicArn.isBlank()) {
            log.warn("SNS_TOPIC_ARN not set -- skipping publish for event_type={}", eventType);
            return;
        }

        PublishResponse response = snsClient.publish(PublishRequest.builder()
                .topicArn(topicArn)
                .message(messageJson)
                .build());

        log.info("event published event_type={} sns_message_id={}", eventType, response.messageId());
    }
}
