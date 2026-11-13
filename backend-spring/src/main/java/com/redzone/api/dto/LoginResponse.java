package com.redzone.api.dto;

import com.redzone.api.model.Usuario;

// O que a API DEVOLVE no login: só o necessário.
public record LoginResponse(Long id, String nome, String email) {

    // Converte um Usuario do banco na resposta do login.
    // Recebe: o usuário. Devolve: um LoginResponse só com id.
    public static LoginResponse de(Usuario usuario) {
        return new LoginResponse(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }
}
