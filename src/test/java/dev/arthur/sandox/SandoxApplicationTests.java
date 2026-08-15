package dev.arthur.sandox;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class SandoxApplicationTests {

	@Test
	void contextLoads() {
	}

}
