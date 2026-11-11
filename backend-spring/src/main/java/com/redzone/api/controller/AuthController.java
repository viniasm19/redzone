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
// @RestController: esta classe recebe requisições HTTP e devolve respostas em JSON.
//   (É um @Controller + @ResponseBody: o que os métodos retornam vira JSON automaticamente.)
// @RequestMapping("..."): o começo do endereço de todas as rotas desta classe.
@RestController
@RequestMapping("/login")
public class AuthController {

    private final UsuarioRepository repository;

    // Injeção de dependência: o Spring entrega o repository pronto (a classe não precisa criar com "new").
    public AuthController(UsuarioRepository repository) {
        this.repository = repository;
    }

    // POST /login -> 200 (dados do usuário, sem senha), 400 (campo vazio) ou 401 (e-mail ou senha errados)
    // @PostMapping: este método atende o POST em /login.
    // @RequestBody: o JSON enviado pelo front vira um objeto LoginRequest.
    // @Valid: confere as regras do LoginRequest (@NotBlank). Se falhar, nem entra aqui: a API responde 400.
    // ResponseEntity<?>: a resposta inteira (código HTTP + corpo). O "?" quer dizer "o corpo pode ser de qualquer tipo".
    @PostMapping
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        // 1) procura o usuário pelo e-mail, ignorando maiúsculas e tirando espaços das pontas (trim)
        Optional<Usuario> usuario = repository.findByEmailIgnoreCase(request.email().trim());

        // Mesma resposta para "e-mail não existe" e "senha errada": quem tenta invadir não descobre
        // quais e-mails estão cadastrados.
        // (Senha em texto puro: aceitável só neste trabalho acadêmico; num sistema real, usar hash/BCrypt.)
        // 2) se não achou o e-mail OU a senha não bate, responde 401 (Unauthorized = "não autorizado")
        if (usuario.isEmpty() || !usuario.get().getSenha().equals(request.senha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensagem", "Email ou senha inválidos."));
        }

        // 3) deu certo: 200 (OK) com id, nome e e-mail. A senha não vai na resposta (LoginResponse não tem senha).
        return ResponseEntity.ok(LoginResponse.de(usuario.get()));
    }
}
