package com.redzone.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Libera o front (porta 4200) para chamar a API.
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    // Regra: só o front em localhost:4200 pode chamar.
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}
