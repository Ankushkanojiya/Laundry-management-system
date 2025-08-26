package com.laundry.config;

import com.laundry.security.JwtAdminFilter;
import com.laundry.security.JwtCustomerFilter;
import com.laundry.service.AdminUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private JwtCustomerFilter jwtCustomerFilter;

    @Autowired
    private JwtAdminFilter jwtAdminFilter;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors-> cors.configurationSource(request -> {
                    CorsConfiguration config=new CorsConfiguration();
                        config.setAllowedOrigins(List.of(frontendUrl));
                    config.setAllowedMethods(List.of("GET","POST","DELETE","PUT","PATCH"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;

                }))
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/customer-auth/login",
                                "/api/customer-auth/register",
                                "/login",
                                "/error",
                                "/api/admin/auth/login",
                                "/health"
                        ).permitAll()
                        .requestMatchers(
                                "/api/orders/customer/**",
                                "/api/payments/**",
                                "/api/customers",
                                "/api/payments/customer",
                                "/api/payments/pending",
                                "/api/customer-auth/me/**",
                                "/api/receipts/**"
                        ).authenticated()
                        .requestMatchers("/api/insights").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Unauthorized\"}");
                        })
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAdminFilter,UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtCustomerFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}