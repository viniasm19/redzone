package com.redzone.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// CORS: por segurança, o navegador bloqueia um site (porta 4200) de chamar outra porta (8080)
// se o servidor não permitir. Aqui o Spring libera o Angular.
// @Configuration: classe de CONFIGURAÇÃO. O Spring a lê ao subir a aplicação.
// implements WebMvcConfigurer: "eu quero ajustar a parte web do Spring". Só sobrescrevemos o que interessa.
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    // @Override: este método já existe no WebMvcConfigurer; aqui damos a nossa versão.
    // Recebe o "registro" de regras de CORS e acrescenta a nossa regra.
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 1) vale para todas as rotas da API ("/**" = qualquer caminho)
        // 2) só o site do Angular (porta 4200) pode chamar
        // 3) só estes métodos HTTP são liberados (OPTIONS é a "pergunta" que o navegador faz antes)
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}
