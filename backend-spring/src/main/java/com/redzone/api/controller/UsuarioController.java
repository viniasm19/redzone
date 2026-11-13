package com.redzone.api.controller;

import com.redzone.api.model.Usuario;
import com.redzone.api.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

// CRUD de usuários em /usuarios. O login fica em /login (AuthController).
// @RestController: esta classe recebe requisições HTTP e devolve respostas em.
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository repository;

    // Injeção de dependência: o Spring entrega o repository pronto.
    public UsuarioController(UsuarioRepository repository) {
        this.repository = repository;
    }

    // GET /usuarios -> lista todos
    // Não recebe nada. Devolve: a lista de usuários (o Spring responde 200 sozinho).
    @GetMapping
    public List<Usuario> listar() {
        return repository.findAll();
    }

    // GET /usuarios/{id} -> 200 ou 404
    // @PathVariable: pega o {id} do endereço (ex.: /usuarios/3 -> id = 3).
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        // findById devolve um Optional (pode estar vazio):
        // .map(...)    -> se achou, transforma em "200 com o usuário"
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /usuarios/email/{email} -> 200 ou 404 (usado na tela "esqueci a senha")
    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> buscarPorEmail(@PathVariable String email) {
        // mesma ideia do buscarPorId.
        return repository.findByEmailIgnoreCase(email.trim())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /usuarios -> 201, ou 409 se o e-mail já estiver cadastrado
    @PostMapping
    // Recebe: o JSON do usuário novo (nome, email, senha).
    public ResponseEntity<?> criar(@Valid @RequestBody Usuario usuario) {
        // 1) se já existe alguém com este e-mail (sem diferença de maiúsculas).
        if (repository.findByEmailIgnoreCase(usuario.getEmail().trim()).isPresent()) {
            return emailDuplicado();
        }

        // 2) apaga um id que o front tenha mandado: assim o save() sempre CRIA (INSERT).
        usuario.setId(null); // quem gera o id é o banco, mesmo que o front envie um
        // 3) salva e responde 201 (Created = "criado") com o usuário salvo (já com o id.
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(usuario));
    }

    // PUT /usuarios/{id} -> 200, 404, ou 409 se o e-mail for de OUTRO usuário.
    // Também serve para trocar a senha: o front manda o usuário com a senha nova.
    @PutMapping("/{id}")
    // Recebe: o id pelo endereço e os dados novos (inclusive a senha) pelo corpo.
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        // 1) o usuário precisa existir: senão, 404
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // Pode manter o próprio e-mail, mas não pode usar o e-mail de outra pessoa
        // 2) procura quem é o "dono" do e-mail informado
        Optional<Usuario> dono = repository.findByEmailIgnoreCase(usuario.getEmail().trim());
        if (dono.isPresent() && !dono.get().getId().equals(id)) {
            return emailDuplicado();
        }

        // 3) usa o id do endereço (não o do corpo) e salva.
        usuario.setId(id); // id já existe -> save() faz UPDATE
        // 4) responde 200 com o usuário atualizado
        return ResponseEntity.ok(repository.save(usuario));
    }

    // DELETE /usuarios/{id} -> 204 ou 404
    // ResponseEntity<Void>: a resposta não tem corpo (só o código HTTP).
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        // 1) não existe: 404
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // 2) apaga e responde 204 (No Content = "deu certo.
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Monta a resposta de e-mail repetido, usada no criar e no atualizar.
    // 409 Conflict: o front mostra "Já existe um usuário com este email."
    private ResponseEntity<Map<String, String>> emailDuplicado() {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("mensagem", "Já existe um usuário com este email."));
    }
}
