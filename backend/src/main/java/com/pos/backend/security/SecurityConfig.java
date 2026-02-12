package com.pos.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security Configuration
 * 
 * This configures Spring Security for your application.
 * 
 * BEGINNER MODE: We're starting with minimal security to make development easier.
 * In Week 4, we'll add proper JWT authentication.
 * 
 * @Configuration marks this as a configuration class
 * @EnableWebSecurity enables Spring Security
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Password Encoder Bean
     * 
     * BCrypt is a one-way hashing algorithm for passwords.
     * NEVER store passwords in plain text!
     * 
     * Example:
     * - User password: "password123"
     * - Stored in DB: "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
     * 
     * Even if someone steals the database, they can't reverse the hash!
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Security Filter Chain
     * 
     * This defines which URLs require authentication.
     * 
     * CURRENT SETUP (Development Mode):
     * - Disables CSRF (needed for REST APIs)
     * - Allows all requests without authentication
     * 
     * FUTURE SETUP (Week 4):
     * - Enable JWT authentication
     * - Protect endpoints based on roles (ADMIN, CASHIER)
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for REST APIs (we'll use JWT tokens instead)
            .csrf(csrf -> csrf.disable())
            
            // Allow ALL requests during development (no authentication needed)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()  // Allow everything for now!
            )
            
            // Allow H2 console to use frames (needed for H2 UI)
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}
