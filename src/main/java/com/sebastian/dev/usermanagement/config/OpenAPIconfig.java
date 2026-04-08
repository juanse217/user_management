package com.sebastian.dev.usermanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenAPIconfig {
    @Bean
    public OpenAPI defineOpenApi(){
        return new OpenAPI()
                    .info(new Info().title("Users management API").description("Users management").version("1.0"));
    }
}
