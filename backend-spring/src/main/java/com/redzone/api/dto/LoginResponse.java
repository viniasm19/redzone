package com.redzone.api.dto;

import com.redzone.api.model.Usuario;

// O que a API DEVOLVE no login: só o necessário. A senha NUNCA volta para o navegador.
public record LoginResponse(Long id, String nome, String email) {

    // Converte um Usuario do banco na resposta do login.
    // Recebe: o usuário. Devolve: um LoginResponse só com id, nome e e-mail (a senha fica de fora).
    // "static": é chamado direto pela classe (LoginResponse.de(usuario)), sem precisar de um objeto antes.
    public static LoginResponse de(Usuario usuario) {
        return new LoginResponse(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }
}
