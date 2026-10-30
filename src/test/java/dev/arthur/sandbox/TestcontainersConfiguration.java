package dev.arthur.sandbox;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

	@Bean
	@ServiceConnection
	PostgreSQLContainer postgresContainer() {
		// The service connection gives Liquibase a connection of its own, without the pool's schema setting, so the
		// URL selects the "producer" schema for every connection, as application.yml does for the app's pool.
		return new PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"))
				.withInitScript("create-producer-schema.sql")
				.withUrlParam("currentSchema", "producer");
	}

}
