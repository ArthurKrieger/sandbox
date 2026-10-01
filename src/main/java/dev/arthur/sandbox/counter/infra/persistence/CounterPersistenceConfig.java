package dev.arthur.sandbox.counter.infra.persistence;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

// sandbox-messaging declares its own @EnableJdbcRepositories, which switches off Boot's repository
// scanning, so this service's repositories are enabled explicitly.
@Configuration(proxyBeanMethods = false)
@EnableJdbcRepositories(basePackageClasses = CounterRowRepository.class)
class CounterPersistenceConfig {
}
