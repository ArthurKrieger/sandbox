package dev.arthur.sandox;

import org.springframework.boot.SpringApplication;

public class TestSandoxApplication {

	public static void main(String[] args) {
		SpringApplication.from(SandoxApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
