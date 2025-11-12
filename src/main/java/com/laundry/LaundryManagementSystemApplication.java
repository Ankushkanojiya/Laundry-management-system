package com.laundry;

import com.laundry.model.Admin;
import com.laundry.repo.AdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@SpringBootApplication
@EntityScan("com.laundry.model")
@EnableAsync
public class LaundryManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(LaundryManagementSystemApplication.class, args);

	}
	@Bean
	CommandLineRunner initAdmin(AdminRepository adminRepo, BCryptPasswordEncoder encoder) {
		return args -> {
			if(adminRepo.count() < 1) {
				Admin admin = new Admin();
				admin.setUsername("admin");
				admin.setPassword(encoder.encode("admin123"));
				adminRepo.save(admin);
			}else{
				System.out.println("Admin already exists");
			}
		};
	}


}
