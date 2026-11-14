package com.redzone.api.dto;

import com.redzone.api.model.Usuario;

// Resposta do login. Sem a senha.
public record LoginResponse(Long id, String nome, String email) {

    public static LoginResponse de(Usuario usuario) {
        return new LoginResponse(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }
}
