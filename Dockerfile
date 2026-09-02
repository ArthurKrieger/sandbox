# Stage 1: Build
FROM gradle:9.5.1-jdk21 AS builder
WORKDIR /app
COPY . .
RUN gradle build -x test --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

# OpenTelemetry Java agent: zero-code auto-instrumentation (Spring Web, JDBC,
# etc.) — always the latest stable release. Behavior (where traces go, which
# signals are enabled) is controlled entirely via OTEL_* env vars at runtime,
# not baked in here — see environments/sandbox/main.tf in sandbox-infra.
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /app/otel-agent.jar

EXPOSE 8080

ENTRYPOINT ["java", "-javaagent:/app/otel-agent.jar", "-jar", "app.jar"]
