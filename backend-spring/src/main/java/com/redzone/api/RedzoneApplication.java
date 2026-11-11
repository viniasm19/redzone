package com.redzone.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Ponto de partida da API: é esta classe que se executa para "ligar" o back-end.
//
// @SpringBootApplication junta três coisas numa anotação só:
//   - liga a configuração automática do Spring Boot (servidor web, banco H2, JSON...);
//   - procura sozinho, neste pacote e nos de dentro (controller, model, repository...),
//     as classes marcadas com @RestController, @Service, @Component etc. e cria os objetos delas;
//   - permite configurar a aplicação nesta própria classe.
@SpringBootApplication
public class RedzoneApplication {

    // main: o primeiro método que o Java executa.
    // SpringApplication.run sobe o servidor na porta 8080 (application.properties) e fica esperando requisições.
    public static void main(String[] args) {
        SpringApplication.run(RedzoneApplication.class, args);
    }
}
