package com.kata.bookstore;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:applicationtest;DB_CLOSE_DELAY=-1")
class BookstoreApplicationTests {

	@Test
	void contextLoads() {
	}

}
