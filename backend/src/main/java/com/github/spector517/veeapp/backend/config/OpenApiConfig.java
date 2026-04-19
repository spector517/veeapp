package com.github.spector517.veeapp.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("VeeApp Backend API")
                        .description("""
                                Local REST API for managing Xray proxy via the VeePeeNET CLI tool.
                                All operations are executed locally on the host where the backend is running.
                                No authentication required — the API is bound to 127.0.0.1.
                                """)
                        .version("0.1.0"));
    }
}