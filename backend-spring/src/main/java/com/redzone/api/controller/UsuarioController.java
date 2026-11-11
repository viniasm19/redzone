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
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository repository;

    public UsuarioController(UsuarioRepository repository) {
        this.repository = repository;
    }

    // GET /usuarios -> lista todos
    @GetMapping
    public List<Usuario> listar() {
        return repository.findAll();
    }

    // GET /usuarios/{id} -> 200 ou 404
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /usuarios/email/{email} -> 200 ou 404 (usado na tela "esqueci a senha")
    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> buscarPorEmail(@PathVariable String email) {
        return repository.findByEmailIgnoreCase(email.trim())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /usuarios -> 201, ou 409 se o e-mail já estiver cadastrado
    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody Usuario usuario) {
        if (repository.findByEmailIgnoreCase(usuario.getEmail().trim()).isPresent()) {
            return emailDuplicado();
        }

        usuario.setId(null); // quem gera o id é o banco, mesmo que o front envie um
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(usuario));
    }

    // PUT /usuarios/{id} -> 200, 404, ou 409 se o e-mail for de OUTRO usuário.
    // Também serve para trocar a senha: o front manda o usuário com a senha nova.
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // Pode manter o próprio e-mail, mas não pode usar o e-mail de outra pessoa
        Optional<Usuario> dono = repository.findByEmailIgnoreCase(usuario.getEmail().trim());
        if (dono.isPresent() && !dono.get().getId().equals(id)) {
            return emailDuplicado();
        }

        usuario.setId(id); // id já existe -> save() faz UPDATE
        return ResponseEntity.ok(repository.save(usuario));
    }

    // DELETE /usuarios/{id} -> 204 ou 404
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // 409 Conflict: o front mostra "Já existe um usuário com este email."
    private ResponseEntity<Map<String, String>> emailDuplicado() {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("mensagem", "Já existe um usuário com este email."));
    }
}
