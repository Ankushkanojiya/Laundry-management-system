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
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


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

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/api/**")
						.allowedOrigins("http://127.0.0.1:5500") // VS Code port
						.allowedMethods("*");
			}
		};
	}
}
