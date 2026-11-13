package com.redzone.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Ponto de partida da API: é esta classe que se executa para "ligar" o back-end.
@SpringBootApplication
public class RedzoneApplication {

    // main: o primeiro método que o Java executa.
    // SpringApplication.run sobe o servidor na porta 8080 (application.properties) e.
    public static void main(String[] args) {
        SpringApplication.run(RedzoneApplication.class, args);
    }
}
