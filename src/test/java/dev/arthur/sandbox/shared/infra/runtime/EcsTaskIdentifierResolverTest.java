package dev.arthur.sandbox.shared.infra.runtime;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class EcsTaskIdentifierResolverTest {

    private HttpServer metadataEndpoint;

    @AfterEach
    void stopMetadataEndpoint() {
        if (metadataEndpoint != null) {
            metadataEndpoint.stop(0);
        }
    }

    @Test
    void resolvesTheShortEcsTaskId() throws IOException {
        final String uri = serveTaskMetadata(
                "{\"TaskARN\":\"arn:aws:ecs:sa-east-1:123:task/cluster/f2189349c0ffee0123456789\"}");

        assertThat(new EcsTaskIdentifierResolver(JsonMapper.builder().build(), uri).resolve()).isEqualTo("f2189349");
    }

    @Test
    void fallsBackToTheHostNameOutsideEcs() throws IOException {
        final String hostName = InetAddress.getLocalHost().getHostName();

        assertThat(new EcsTaskIdentifierResolver(JsonMapper.builder().build(), null).resolve()).isEqualTo(hostName);
    }

    @Test
    void fallsBackToTheHostNameWhenTheMetadataEndpointFails() throws IOException {
        final String hostName = InetAddress.getLocalHost().getHostName();

        assertThat(new EcsTaskIdentifierResolver(JsonMapper.builder().build(), "http://127.0.0.1:1").resolve())
                .isEqualTo(hostName);
    }

    private String serveTaskMetadata(String body) throws IOException {
        metadataEndpoint = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        metadataEndpoint.createContext("/v4/task", exchange -> {
            final byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream response = exchange.getResponseBody()) {
                response.write(bytes);
            }
        });
        metadataEndpoint.start();
        return "http://127.0.0.1:" + metadataEndpoint.getAddress().getPort() + "/v4";
    }
}
