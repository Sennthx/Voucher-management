package com.wecan.voucher.management;

import com.wecan.voucher.management.voucherSystem.repository.VoucherRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class VoucherApplicationTests {

	@Autowired
	private ApplicationContext context;

	@Autowired
	private VoucherRepository voucherRepository;

	@Test
	void contextLoads() {
		assertNotNull(context, "Application context should be loaded");
		assertNotNull(voucherRepository, "VoucherRepository should be available in the context");
	}

	@Test
	void databaseConnectivityTest() {
		assertTrue(voucherRepository.count() >= 0, "Repository should interact with the database successfully");
	}
}