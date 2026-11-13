package com.redzone.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// CORS: por segurança.
// se o servidor não permitir. Aqui o Spring libera o Angular.
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    // @Override: este método já existe no WebMvcConfigurer; aqui damos a nossa versão.
    // Recebe o "registro" de regras de CORS e acrescenta a nossa regra.
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 1) vale para todas as rotas da API ("/**" = qualquer caminho)
        // 2) só o site do Angular (porta 4200) pode chamar
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}
