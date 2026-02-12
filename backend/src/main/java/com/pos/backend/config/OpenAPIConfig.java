package com.pos.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger Configuration
 * 
 * This configures the auto-generated API documentation.
 * Access at: http://localhost:8080/swagger-ui.html
 * 
 * @Configuration tells Spring this class contains configuration
 * @Bean methods return objects that Spring manages
 */
@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI posSystemOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Point-of-Sale System API")
                        .description("RESTful API for modern POS system supporting sales, inventory, and cashier management")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Your Name")
                                .email("your.email@example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
