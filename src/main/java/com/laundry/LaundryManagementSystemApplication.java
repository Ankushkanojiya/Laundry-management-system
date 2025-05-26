package com.laundry;

import com.laundry.model.Admin;
import com.laundry.repo.AdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EnableJpaRepositories("com.laundry.repo") // Add if missing
@EntityScan("com.laundry.model") // Add if missing
public class LaundryManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(LaundryManagementSystemApplication.class, args);

	}
	@Bean
	CommandLineRunner initAdmin(AdminRepository adminRepo) {
		return args -> {
			Admin admin = new Admin();
			admin.setUsername("admin");
			admin.setPassword("admin123");
			adminRepo.save(admin);
		};
	}
}
