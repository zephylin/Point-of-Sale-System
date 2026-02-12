package com.pos.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Application Class
 * 
 * This is the entry point of your Spring Boot application.
 * 
 * @SpringBootApplication is a convenience annotation that combines:
 * - @Configuration: Makes this a configuration class
 * - @EnableAutoConfiguration: Tells Spring Boot to auto-configure based on dependencies
 * - @ComponentScan: Scans for components (controllers, services, etc.) in this package
 * 
 * When you run this class, Spring Boot:
 * 1. Starts an embedded Tomcat server (default port 8080)
 * 2. Scans for all @Controller, @Service, @Repository annotations
 * 3. Sets up database connection
 * 4. Initializes security
 * 5. Creates REST endpoints
 */
@SpringBootApplication
public class PointOfSaleApplication {

    public static void main(String[] args) {
        // This starts the entire Spring Boot application
        SpringApplication.run(PointOfSaleApplication.class, args);
        
        System.out.println("\n========================================");
        System.out.println("🚀 Point-of-Sale Backend Started!");
        System.out.println("📖 API Documentation: http://localhost:8080/swagger-ui.html");
        System.out.println("🗄️  H2 Console: http://localhost:8080/h2-console");
        System.out.println("========================================\n");
    }
}
