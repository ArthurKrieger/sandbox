package dev.arthur.sandbox.shared.infra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

// Explicit: sandbox-messaging's own @EnableJdbcRepositories switches off Spring Boot's default scan.
@Configuration(proxyBeanMethods = false)
@EnableJdbcRepositories(basePackages = "dev.arthur.sandbox")
class PersistenceConfiguration {
}
