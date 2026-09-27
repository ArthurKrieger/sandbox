FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Explicit, not relying on whatever busybox applets this particular alpine
# base happens to ship -- used by sandbox-infra's ECS container healthCheck.
RUN apk add --no-cache curl

# The jar is already built by CI's own "Build application" step before this
# runs (./gradlew build -x test) -- rebuilding it again inside the Docker
# build was redundant (twice the compile time) and broke outright once
# build.gradle needed MAVEN_S3_BUCKET/AWS credentials, which an isolated
# Docker build context doesn't inherit from the CI job's environment.
COPY build/libs/*.jar app.jar

# OpenTelemetry Java agent: zero-code auto-instrumentation (Spring Web, JDBC,
# etc.) — always the latest stable release. Behavior (where traces go, which
# signals are enabled) is controlled entirely via OTEL_* env vars at runtime,
# not baked in here — see environments/sandbox/main.tf in sandbox-infra.
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /app/otel-agent.jar

EXPOSE 8080

ENTRYPOINT ["java", "-javaagent:/app/otel-agent.jar", "-jar", "app.jar"]
