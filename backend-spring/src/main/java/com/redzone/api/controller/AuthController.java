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

// Login em /login. Quem confere e-mail e senha é o back-end.
// @RestController: esta classe recebe requisições HTTP e devolve respostas em.
@RestController
@RequestMapping("/login")
public class AuthController {

    private final UsuarioRepository repository;

    // Injeção de dependência.
    public AuthController(UsuarioRepository repository) {
        this.repository = repository;
    }

    // POST /login -> 200 (dados do usuário, sem senha).
    // @PostMapping: este método atende o POST em /login.
    @PostMapping
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        // 1) procura o usuário pelo e-mail.
        Optional<Usuario> usuario = repository.findByEmailIgnoreCase(request.email().trim());

        // Mesma resposta para "e-mail não existe" e "senha errada".
        // quais e-mails estão cadastrados.
        if (usuario.isEmpty() || !usuario.get().getSenha().equals(request.senha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensagem", "Email ou senha inválidos."));
        }

        // 3) deu certo: 200 (OK) com id, nome e e-mail.
        return ResponseEntity.ok(LoginResponse.de(usuario.get()));
    }
}
