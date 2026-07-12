package com.taskapp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TaskApp API")
                        .description("API REST para una aplicación tipo Trello simplificada — Tableros colaborativos, tareas, subtareas, comentarios y tareas personales.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("TaskApp Team")
                                .email("support@taskapp.com"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
