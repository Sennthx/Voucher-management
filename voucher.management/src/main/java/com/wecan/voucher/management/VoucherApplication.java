package com.wecan.voucher.management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

@SpringBootApplication
public class VoucherApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(VoucherApplication.class);

	private final DataSource dataSource;

	public VoucherApplication(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public static void main(String[] args) {
		SpringApplication.run(VoucherApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("Successfully connected to database: {}", dataSource.getConnection().getMetaData().getURL());

		log.debug("Database product: {}", dataSource.getConnection().getMetaData().getDatabaseProductName());

		log.debug("Driver: {}", dataSource.getConnection().getMetaData().getDriverName());
	}

}
