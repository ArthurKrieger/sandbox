package dev.arthur.sandbox.shared.infra.runtime;

import dev.arthur.sandbox.shared.application.port.InstanceIdentifierResolver;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Slf4j
@Component("instanceIdentifierResolver")
class EcsTaskIdentifierResolver implements InstanceIdentifierResolver {

    private static final int SHORT_ID_LENGTH = 8;

    private final String name;

    @Autowired
    EcsTaskIdentifierResolver(@NonNull ObjectMapper objectMapper) {
        this(objectMapper, System.getenv("ECS_CONTAINER_METADATA_URI_V4"));
    }

    EcsTaskIdentifierResolver(@NonNull ObjectMapper objectMapper, @Nullable String metadataUri) {
        this.name = resolve(objectMapper, metadataUri);
        log.info("instance identifier: {}", name);
    }

    @Override
    public String resolve() {
        return name;
    }

    private static String resolve(@NonNull ObjectMapper objectMapper, @Nullable String metadataUri) {
        if (metadataUri != null && !metadataUri.isBlank()) {
            try (HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build()) {
                final HttpRequest request = HttpRequest.newBuilder(URI.create(metadataUri + "/task"))
                        .timeout(Duration.ofSeconds(2)).build();
                final JsonNode task = objectMapper.readTree(
                        client.send(request, HttpResponse.BodyHandlers.ofString()).body());
                final String taskArn = task.path("TaskARN").asString();
                final String taskId = taskArn.substring(taskArn.lastIndexOf('/') + 1);
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
