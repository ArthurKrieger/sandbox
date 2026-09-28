FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

ARG OTEL_AGENT_VERSION=2.31.1

# Used by the ECS container health check.
RUN apk add --no-cache curl \
    && addgroup -S app && adduser -S app -G app

# Expects the jar already built by `./gradlew build`; it is not built inside Docker.
COPY --chown=app:app build/libs/*.jar app.jar

# Configured at runtime through OTEL_* env vars.
ADD --chown=app:app https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_AGENT_VERSION}/opentelemetry-javaagent.jar /app/otel-agent.jar

USER app
EXPOSE 8080

ENTRYPOINT ["java", "-javaagent:/app/otel-agent.jar", "-jar", "app.jar"]
