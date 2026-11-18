package com.redzone.api.dto;

import com.redzone.api.modelo.Usuario;

// Resposta do login. Sem a senha.
public record LoginResposta(Long id, String nome, String email) {

    public static LoginResposta de(Usuario usuario) {
        return new LoginResposta(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }
}
