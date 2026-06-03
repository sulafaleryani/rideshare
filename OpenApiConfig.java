package com.rideshare.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "RideShare API",
                version = "1.0.0",
                description = "Production-quality Uber-like ride sharing REST API",
                contact = @Contact(name = "RideShare Team", email = "api@rideshare.com"),
                license = @License(name = "MIT License")
        ),
        servers = {
                @Server(url = "/api", description = "Default Server")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Provide a valid JWT token obtained from the /auth/login endpoint"
)
public class OpenApiConfig {
}
