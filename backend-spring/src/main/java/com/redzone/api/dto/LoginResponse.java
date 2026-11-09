package com.redzone.api.dto;

import com.redzone.api.model.Usuario;

// O que a API DEVOLVE no login: só o necessário. A senha NUNCA volta para o navegador.
public record LoginResponse(Long id, String nome, String email) {

    public static LoginResponse de(Usuario usuario) {
        return new LoginResponse(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }
}
