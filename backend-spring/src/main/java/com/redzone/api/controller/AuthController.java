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

// Login em /login. Quem confere e-mail e senha é o back-end: o front não recebe mais a lista de usuários.
@RestController
@RequestMapping("/login")
public class AuthController {

    private final UsuarioRepository repository;

    public AuthController(UsuarioRepository repository) {
        this.repository = repository;
    }

    // POST /login -> 200 (dados do usuário, sem senha), 400 (campo vazio) ou 401 (e-mail ou senha errados)
    @PostMapping
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Optional<Usuario> usuario = repository.findByEmailIgnoreCase(request.email().trim());

        // Mesma resposta para "e-mail não existe" e "senha errada": quem tenta invadir não descobre
        // quais e-mails estão cadastrados.
        // (Senha em texto puro: aceitável só neste trabalho acadêmico; num sistema real, usar hash/BCrypt.)
        if (usuario.isEmpty() || !usuario.get().getSenha().equals(request.senha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensagem", "Email ou senha inválidos."));
        }

        return ResponseEntity.ok(LoginResponse.de(usuario.get()));
    }
}
