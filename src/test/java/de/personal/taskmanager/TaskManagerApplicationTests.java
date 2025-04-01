package de.personal.taskmanager;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class TaskManagerApplicationTests {

	private static final Logger log = LoggerFactory.getLogger(TaskManagerApplicationTests.class);

	@Test
	void contextLoads() {
		log.info(("✅ Spring Boot context loaded successfully!"));
	}

	@Test
	void pushTrigger() {
		log.info("A new push trigger jenkins' build successfully!");
	}

}
