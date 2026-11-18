package com.redzone.api.controlador;

import com.redzone.api.dto.LoginRequisicao;
import com.redzone.api.dto.LoginResposta;
import com.redzone.api.modelo.Usuario;
import com.redzone.api.repositorio.UsuarioRepositorio;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

// Rota de login: POST /login.
@RestController
@RequestMapping("/login")
public class LoginControlador {

    private final UsuarioRepositorio repositorio;

    public LoginControlador(UsuarioRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @PostMapping
    // Confere e-mail e senha. 200 se certo, 401 se errado.
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequisicao request) {
        Optional<Usuario> usuario = repositorio.findByEmailIgnoreCase(request.email().trim());

        if (usuario.isEmpty() || !usuario.get().getSenha().equals(request.senha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensagem", "Email ou senha inválidos."));
        }

        return ResponseEntity.ok(LoginResposta.de(usuario.get()));
    }
}
