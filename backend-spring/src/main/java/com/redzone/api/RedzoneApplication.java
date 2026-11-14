package com.redzone.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Ponto de partida: liga a API na porta 8080.
@SpringBootApplication
public class RedzoneApplication {

    // Método que o Java executa primeiro.
    public static void main(String[] args) {
        SpringApplication.run(RedzoneApplication.class, args);
    }
}
