package dev.arthur.sandbox.counter.infra.runtime;

import dev.arthur.sandbox.counter.application.PodIdentity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * On ECS the pod is the task: its id comes from the task metadata endpoint Fargate injects through
 * ECS_CONTAINER_METADATA_URI_V4. Anywhere else (local runs) it falls back to the host name.
 */
@Slf4j
@Component
class EcsPodIdentity implements PodIdentity {

    private static final int SHORT_ID_LENGTH = 8;

    private final String name;

    EcsPodIdentity(ObjectMapper objectMapper) {
        this.name = resolve(objectMapper);
        log.info("pod identity: {}", name);
    }

    @Override
    public String name() {
        return name;
    }

    private static String resolve(ObjectMapper objectMapper) {
        String metadataUri = System.getenv("ECS_CONTAINER_METADATA_URI_V4");
        if (metadataUri != null && !metadataUri.isBlank()) {
            try (HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build()) {
                HttpRequest request = HttpRequest.newBuilder(URI.create(metadataUri + "/task"))
                        .timeout(Duration.ofSeconds(2)).build();
                JsonNode task = objectMapper.readTree(client.send(request, HttpResponse.BodyHandlers.ofString()).body());
                String taskArn = task.path("TaskARN").asString();
                String taskId = taskArn.substring(taskArn.lastIndexOf('/') + 1);
                return taskId.substring(0, Math.min(SHORT_ID_LENGTH, taskId.length()));
            } catch (Exception e) {
                log.warn("could not read ECS task metadata, falling back to host name", e);
            }
        }
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown";
        }
    }
}
