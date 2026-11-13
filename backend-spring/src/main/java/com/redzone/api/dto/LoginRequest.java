package com.redzone.api.dto;

import jakarta.validation.constraints.NotBlank;

// O que o front ENVIA no POST /login: o e-mail e a senha digitados.
public record LoginRequest(
        @NotBlank(message = "Informe o email.")
        String email,

        @NotBlank(message = "Informe a senha.")
        String senha
) {
}
