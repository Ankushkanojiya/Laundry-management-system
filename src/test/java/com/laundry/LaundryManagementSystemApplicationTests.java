package com.laundry;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.mail.host=disabled")
class LaundryManagementSystemApplicationTests {

	@Test
	void contextLoads() {
	}

}
