package com.redzone.api.controller;

import com.redzone.api.dto.LoginRequest;
import com.redzone.api.dto.LoginResponse;
import com.redzone.api.model.Usuario;
import com.redzone.api.repository.UsuarioRepository;
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
public class AuthController {

    private final UsuarioRepository repository;

    public AuthController(UsuarioRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    // Confere e-mail e senha. 200 se certo, 401 se errado.
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Optional<Usuario> usuario = repository.findByEmailIgnoreCase(request.email().trim());

        if (usuario.isEmpty() || !usuario.get().getSenha().equals(request.senha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensagem", "Email ou senha inválidos."));
        }

        return ResponseEntity.ok(LoginResponse.de(usuario.get()));
    }
}
