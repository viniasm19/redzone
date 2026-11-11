package com.redzone.api.dto;

import jakarta.validation.constraints.NotBlank;

// O que o front ENVIA no POST /login: o e-mail e a senha digitados.
//
// O que é DTO? "Data Transfer Object", um objeto só para TRANSPORTAR dados entre o front e a API.
// Ele não é uma tabela: serve para dizer exatamente quais campos entram (ou saem) numa rota.
//
// O que é record? Uma classe enxuta, só com dados. O Java cria sozinho o construtor,
// os "getters" (aqui eles se chamam email() e senha(), sem o "get") e o toString.
// @NotBlank em cada campo: o @Valid do controller recusa (400) se vier vazio.
public record LoginRequest(
        @NotBlank(message = "Informe o email.")
        String email,

        @NotBlank(message = "Informe a senha.")
        String senha
) {
}
