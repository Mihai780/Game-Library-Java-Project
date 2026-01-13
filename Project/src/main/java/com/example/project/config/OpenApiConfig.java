package com.example.project.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Game Management API", version = "1.0", description = "REST API for managing games and users."))
public class OpenApiConfig {
}
