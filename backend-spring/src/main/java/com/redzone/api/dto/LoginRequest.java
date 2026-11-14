package com.redzone.api.dto;

import jakarta.validation.constraints.NotBlank;

// Dados do login: e-mail e senha.
public record LoginRequest(
        @NotBlank(message = "Informe o email.")
        String email,

        @NotBlank(message = "Informe a senha.")
        String senha
) {
}
